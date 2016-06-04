/*
 * Copyright 2016 Zot201
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package zotmc.onlysilver.config;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import io.netty.buffer.ByteBuf;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import zotmc.onlysilver.util.Fields;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import javax.annotation.Nullable;

public abstract class AbstractConfig<T extends AbstractConfig<T>> {

  // post by the host mod to notify config the server state
  public static class NotifyServerStart extends Event { }
  public static class NotifyServerStop extends Event { }

  // post by config to notify changes
  @SuppressWarnings("WeakerAccess")
  public static class Init extends Event { }
  @SuppressWarnings("WeakerAccess")
  public static class Change extends Event { }
  public static class Accept extends Change { }
  public static class Discard extends Change { }

  @SuppressWarnings("WeakerAccess")
  static final int IN_FILE = 0, LOCAL = 1, CURRENT = 2;
  private Holder holder;

  protected static <E, F extends E> Property<E> base(F value) {
    return new BaseProperty<E>(value);
  }

  protected abstract GsonBuilder getGsonBuilder();

  final void initConfig(String name, EventBus bus, File file) {
    SimpleNetworkWrapper network = new SimpleNetworkWrapper(name);
    @SuppressWarnings("unchecked") Class<T> clz = (Class<T>) getClass();

    T[] a;
    {
      List<Field> fields = Lists.newArrayList(getAnnotatedFields(clz, Instances.class));
      checkArgument(fields.size() == 1, "A unique @Instances field is required: %s", fields);

      Field f = fields.get(0);
      int mod = f.getModifiers();
      boolean flag = Modifier.isPrivate(mod) && Modifier.isStatic(mod) && Modifier.isFinal(mod);
      checkArgument(flag, "Not private static final: %s", f);

      a = ObjectArrays.newArray(clz, CURRENT + 1);
      checkArgument(a.getClass().isAssignableFrom(f.getType()), "Not an array of T: %s", f);
      f.setAccessible(true);
      T[] old = Fields.get(null, f);
      checkState(old == null || old.length == 0, "@Instance field is occupied: %s", f);
      Fields.setFinal(null, f, a);
    }

    {
      holder = new Holder(file, LogManager.getFormatterLogger(name));
      a[IN_FILE] = newInstance();
      a[LOCAL] = newInstance();

      for (Field f : getFieldsOfType(clz, Property.class)) {
        checkArgument(!Modifier.isStatic(f.getModifiers()), "Not an instance field: %s", f);
        checkArgument(Modifier.isFinal(f.getModifiers()), "Not final: %s", f);
        checkArgument(f.getGenericType() instanceof ParameterizedType, "Not in parameterized type: %s", f);
        f.setAccessible(true);

        BaseProperty<?> base = Fields.get(this, f);
        Property<?> p = new UserProperty<>(checkNotNull(base));
        Fields.setFinal(a[IN_FILE], f, p);

        if (f.getAnnotation(Restart.class) != null) p = new UserProperty<>(base);
        Fields.setFinal(a[LOCAL], f, p);
      }

      a[CURRENT] = a[LOCAL];
    }

    EventSubscriber<T> subscriber = new EventSubscriber<>(network, bus, a, holder.log);
    network.registerMessage(subscriber, Message.class, 0, Side.CLIENT);
    bus.register(subscriber);
    MinecraftForge.EVENT_BUS.register(subscriber);

    a[IN_FILE].loadFromFile();
    a[LOCAL].apply(a[IN_FILE]);
    bus.post(new Init());
  }

  final void loadFromFile() {
    File file = holder.file;

    if (file.exists()) {
      try {
        T temp = newInstance();
        getGsonBuilder()
          .registerTypeAdapter(getClass(), new Deserializer<>(temp))
          .create()
          .fromJson(Files.toString(file, Charsets.UTF_8), getClass());
        apply(temp);

      } catch (JsonSyntaxException | IOException e) {
        holder.log.error("Error loading config from file", e);

        try {
          Files.move(file, new File(file.getParent(), file.getName() + ".erred"));
        } catch (IOException ignored) { }
      }
    }
  }

  final void saveToFile() {
    String json = getGsonBuilder().setPrettyPrinting().create().toJson(toMap(false));
    try {
      Files.write(json, holder.file, Charsets.UTF_8);
    } catch (IOException e) {
      holder.log.error("Error saving config to file", e);
    }
  }

  final T copy() {
    T ret = newInstance();
    for (Field f : getFieldsOfType(getClass(), Property.class)) {
      f.setAccessible(true);
      //noinspection ConstantConditions
      Fields.<Property<?>>setFinal(ret, f, Fields.<Property<?>>get(this, f).copy());
    }
    return ret;
  }

  final void clear() {
    for (Field f : getFieldsOfType(getClass(), Property.class)) {
      f.setAccessible(true);
      //noinspection ConstantConditions
      Fields.<Property<?>>get(this, f).setRaw(null);
    }
  }

  final void apply(T config) {
    for (Field f : getFieldsOfType(getClass(), Property.class)) {
      f.setAccessible(true);
      //noinspection ConstantConditions
      Fields.<Property<Object>>get(this, f).setRaw(Fields.<Property<?>>get(config, f).getRaw());
    }
  }

  private T newInstance() {
    try {
      @SuppressWarnings("unchecked")
      Constructor<T> ctor = (Constructor<T>) getClass().getDeclaredConstructor();
      ctor.setAccessible(true);
      T ret = ctor.newInstance();
      ((AbstractConfig<T>) ret).holder = checkNotNull(holder);
      return ret;
    } catch (Throwable t) {
      throw Throwables.propagate(t);
    }
  }

  private Map<String, Object> toMap(boolean isNetwork) {
    Map<String, Object> ret = Maps.newLinkedHashMap();
    for (Field f : getFieldsOfType(getClass(), Property.class))
      if (!isNetwork || f.getAnnotation(Local.class) == null && f.getAnnotation(Restart.class) == null) {
        f.setAccessible(true);
        Property<?> p = Fields.get(this, f);
        //noinspection ConstantConditions
        ret.put(f.getName(), isNetwork ? p.get() : p.getRaw());
      }
    return ret;
  }

  private static Iterable<Field> getAnnotatedFields(Class<?> clz, final Class<? extends Annotation> annotationClass) {
    List<Field> unfiltered = Arrays.asList(clz.getDeclaredFields());
    //noinspection StaticPseudoFunctionalStyleMethod
    return Iterables.filter(unfiltered, input -> input.getAnnotation(annotationClass) != null);
  }

  private static Iterable<Field> getFieldsOfType(Class<?> clz, final Class<?> type) {
    List<Field> unfiltered = Arrays.asList(clz.getDeclaredFields());
    //noinspection StaticPseudoFunctionalStyleMethod
    return Iterables.filter(unfiltered, input -> type.isAssignableFrom(input.getType()));
  }


  private static class Holder {
    final File file;
    final Logger log;

    public Holder(File file, Logger log) {
      this.file = file;
      this.log = log;
    }
  }

  private static class EventSubscriber<T extends AbstractConfig<T>> implements IMessageHandler<Message, IMessage> {
    private final SimpleNetworkWrapper network;
    private final EventBus bus;
    private final T[] configs;
    private final Logger log;

    EventSubscriber(SimpleNetworkWrapper network, EventBus bus, T[] configs, Logger log) {
      this.network = network;
      this.bus = bus;
      this.configs = configs;
      this.log = log;
    }

    @Override public @Nullable IMessage onMessage(Message message, @Nullable MessageContext unused) {
      if (message.throwable != null) log.catching(message.throwable);
      else {
        T[] a = configs;

        if (a[CURRENT] == a[LOCAL]) {
          log.info("Accept server config: %s", message.json);

          T current = a[LOCAL].copy();
          Class<?> clz = current.getClass();
          try {
            current.getGsonBuilder()
              .registerTypeAdapter(clz, new Deserializer<>(current))
              .create()
              .fromJson(message.json, clz);

          } catch (Throwable t) {
            log.catching(t);
            current = a[LOCAL].copy();
          }

          a[CURRENT] = current;
          bus.post(new Accept());
        }
      }

      return null;
    }

    @SubscribeEvent public void onServerStart(NotifyServerStart event) {
      configs[IN_FILE].loadFromFile();
      onMessage(new Message(configs[LOCAL]), null);
    }
    @SubscribeEvent public void onServerStop(NotifyServerStop event) {
      onServerDisconnected(null);
    }

    @SubscribeEvent public void onClientConnected(PlayerLoggedInEvent event) {
      if (event.player instanceof EntityPlayerMP)
        network.sendTo(new Message(configs[LOCAL]), (EntityPlayerMP) event.player);
    }
    @SubscribeEvent public void onServerDisconnected(@Nullable ClientDisconnectionFromServerEvent unused) {
      if (configs[CURRENT] != configs[LOCAL]) {
        configs[CURRENT] = configs[LOCAL];
        bus.post(new Discard());
      }
    }
  }

  static class Message implements IMessage {
    private String json;
    private Throwable throwable;

    @SuppressWarnings("unused")
    @Deprecated public Message() { }
    private Message(AbstractConfig<?> config) {
      json = config.getGsonBuilder().create().toJson(config.toMap(true));
    }

    @Override public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, json);
    }
    @Override public void fromBytes(ByteBuf buf) {
      try {
        json = ByteBufUtils.readUTF8String(buf);
      } catch (Throwable t) {
        throwable = t;
      }
    }
  }


  public static abstract class Property<E> {
    private Property() { }
    public abstract E get();
    abstract E getRaw();
    abstract void set(E e);
    abstract void setRaw(@Nullable E e);
    public abstract BaseProperty<E> base();
    abstract Property<E> copy();

    static <E> E notNull(@Nullable E e) {
      if (e == null) throw new IllegalArgumentException();
      return e;
    }
  }

  @SuppressWarnings("WeakerAccess")
  public static class BaseProperty<E> extends Property<E> {
    private final E base;
    private E raw;
    private BaseProperty(E base) {
      this.base = base;
    }
    @Override public E get() {
      E e = raw;
      return e != null ? e : base;
    }
    @Override public E getRaw() {
      return raw;
    }
    @Override public void set(E e) {
      raw = !Objects.equal(e, base) ? notNull(e) : null;
    }
    @Override public void setRaw(E e) {
      raw = e;
    }
    @Deprecated @Override public BaseProperty<E> base() {
      return this;
    }
    @Override Property<E> copy() {
      BaseProperty<E> ret = new BaseProperty<>(base);
      ret.raw = raw;
      return ret;
    }
  }

  private static class UserProperty<E> extends Property<E> {
    private final BaseProperty<E> parent;
    private E raw;
    UserProperty(BaseProperty<E> parent) {
      this.parent = parent;
    }
    @Override public E get() {
      E e = raw;
      return e != null ? e : parent.get();
    }
    @Override E getRaw() {
      return raw;
    }
    @Override void set(E e) {
      raw = !Objects.equal(e, parent.get()) ? notNull(e) : null;
    }
    @Override void setRaw(@Nullable E e) {
      raw = e;
    }
    @Override public BaseProperty<E> base() {
      return parent;
    }
    @Override Property<E> copy() {
      UserProperty<E> ret = new UserProperty<>(parent);
      ret.raw = raw;
      return ret;
    }
  }

  private static class Deserializer<T extends AbstractConfig<T>> implements JsonDeserializer<T> {
    private final T config;
    Deserializer(T config) {
      this.config = config;
    }

    @Override public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      if (!(json instanceof JsonObject)) throw new JsonParseException("JsonObject expected");
      JsonObject jsonObject = (JsonObject) json;

      for (Field f : getFieldsOfType(config.getClass(), Property.class)) {
        Type type = ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
        JsonElement j = jsonObject.get(f.getName());

        if (j != null) {
          Object obj = context.deserialize(j, type);
          f.setAccessible(true);
          //noinspection ConstantConditions
          Fields.<Property<Object>>get(config, f).setRaw(obj);
        }
      }

      return config;
    }
  }


  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  @interface Instances { }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  @interface Local { }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  @interface Restart { }

}
