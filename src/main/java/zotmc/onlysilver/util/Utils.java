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
package zotmc.onlysilver.util;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.*;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.MissingModsException;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import zotmc.onlysilver.util.init.MethodInfo;
import zotmc.onlysilver.util.init.SimpleVersion;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings({"WeakerAccess", "Guava"})
public class Utils {

  public static final SimpleVersion MC_VERSION =
      new SimpleVersion(Fields.get(null, findField(Loader.class, "MC_VERSION"))); // prevent inlining


  // Minecraft

  public static String getEntityString(Class<? extends Entity> clz) {
    return EntityList.CLASS_TO_NAME.get(clz);
  }

  public static boolean isClientSide() {
    return FMLCommonHandler.instance().getSide().isClient();
  }

  public static Localization localize(String unlocalized) {
    return new Localization(unlocalized);
  }
  public static Localization localize(String unlocalized, Object... args) {
    Object[] a = new Object[args.length];
    for (int i = 0; i < a.length; i++) {
      Object o = args[i];
      a[i] = !(o instanceof String) ? o : new Localization((String) o);
    }
    return new Localization(unlocalized, a);
  }

  public static class Localization implements Supplier<String> {
    private static final Object[] ZERO_LENGTH_ARRAY = new Object[0];
    private final String unlocalized;
    private final Object[] args;

    private Localization(String unlocalized) {
      this(unlocalized, ZERO_LENGTH_ARRAY);
    }
    private Localization(String unlocalized, Object[] args) {
      this.unlocalized = checkNotNull(unlocalized);
      this.args = checkNotNull(args);
    }

    @Override public String get() {
      return toString();
    }
    @Override public String toString() {
      return I18n.format(unlocalized, args);
    }
    public String toString(Object... args) {
      return I18n.format(unlocalized, ObjectArrays.concat(this.args, args, Object.class));
    }

    public Supplier<String> append(final Object obj) {
      return () -> Localization.this.get().concat(String.valueOf(obj));
    }
  }

  public static int getEnchLevel(ItemStack item, Enchantment ench) {
    return EnchantmentHelper.getEnchantmentLevel(ench, item);
  }

  public static boolean hasEnch(EntityItem item, Feature<? extends Enchantment> ench) {
    return ench.exists() && hasEnch(item.getEntityItem(), ench.get());
  }
  public static boolean hasEnch(ItemStack item, Feature<? extends Enchantment> ench) {
    return ench.exists() && hasEnch(item, ench.get());
  }
  public static boolean hasEnch(@Nullable ItemStack item, Enchantment ench) {
    if (item != null) {
      int id = Enchantment.getEnchantmentID(ench);
      NBTTagList nbt = item.getItem() == Items.ENCHANTED_BOOK ?
          Items.ENCHANTED_BOOK.getEnchantments(item) : item.getEnchantmentTagList();

      //noinspection ConstantConditions
      if (nbt != null)
        for (int i = 0; i < nbt.tagCount(); i++)
          if (nbt.getCompoundTagAt(i).getShort("id") == id)
            return true;
    }

    return false;
  }


  // Maths

  public static final float PI = (float) Math.PI;

  public static float previous(float a, double step) {
    double r = Math.rint(a / step);
    float f = (float) (step * r);
    if (f >= a) f = (float) (step * (r - 1));
    if (f == a) f = Math.nextAfter(a, -Float.MAX_VALUE);
    return f;
  }

  public static float next(float a, double step) {
    double r = Math.rint(a / step);
    float f = (float) (step * r);
    if (f <= a) f = (float) (step * (r + 1));
    if (f == a) f = Math.nextAfter(a, +Float.MAX_VALUE);
    return f;
  }

  public static double max(double... a) {
    double ret = Double.NEGATIVE_INFINITY;
    for (double f : a)
      ret = Math.max(ret, f);
    return ret;
  }

  public static double min(double... a) {
    double ret = Double.POSITIVE_INFINITY;
    for (double f : a)
      ret = Math.min(ret, f);
    return ret;
  }


  // misc

  public static <T> Feature<T> missingFeature() {
    //noinspection unchecked
    return (Feature<T>) MissingFeature.INSTANCE;
  }
  private enum MissingFeature implements Feature<Object> {
    INSTANCE;
    @Override public boolean exists() {
      return false;
    }
    @Override public Object get() {
      throw new IllegalStateException();
    }
  }

  public static <T> Consumer<T> doNothing() {
    //noinspection unchecked
    return (Consumer<T>) EmptyConsumer.INSTANCE;
  }
  private enum EmptyConsumer implements Consumer<Object> {
    INSTANCE;
    @Override public void accept(Object t) { }
  }

  public static <T> ThreadLocal<T> newThreadLocal(final T reusableInitialValue) {
    return new ThreadLocal<T>() {
      @Override protected T initialValue() {
        return reusableInitialValue;
      }
    };
  }


  // collections

  public static <K, V> Map<K, V> newIdentityHashMap() {
    return new NullSafeMap<>(Maps.newIdentityHashMap());
  }

  public static <K, V> Map<K, V> newHashMap() {
    return new NullSafeMap<>(Maps.newHashMap());
  }

  public static <E> Set<E> newIdentityHashSet() {
    return Sets.newSetFromMap(Utils.newIdentityHashMap());
  }

  private static class NullSafeMap<K, V> extends ForwardingMap<K, V> {
    private final Map<K, V> delegate;
    private Set<Entry<K, V>> entrySet;

    private NullSafeMap(Map<K, V> delegate) {
      this.delegate = delegate;
    }
    @Override protected Map<K, V> delegate() {
      return delegate;
    }

    @Override public V put(K key, V value) {
      return super.put(checkNotNull(key), checkNotNull(value));
    }
    @Override public void putAll(Map<? extends K, ? extends V> map) {
      standardPutAll(map);
    }

    @Override public Set<Entry<K, V>> entrySet() {
      if (entrySet != null) return entrySet;
      final Set<Entry<K, V>> delegate = this.delegate.entrySet();

      return entrySet = new ForwardingSet<Entry<K,V>>() {
        @Override protected Set<Entry<K, V>> delegate() {
          return delegate;
        }

        @Override public boolean add(Entry<K, V> element) {
          throw new UnsupportedOperationException();
        }
        @Override public boolean addAll(Collection<? extends Entry<K, V>> collection) {
          throw new UnsupportedOperationException();
        }

        @Override public Iterator<Entry<K, V>> iterator() {
          final Iterator<Entry<K, V>> delegate = super.iterator();

          return new ForwardingIterator<Entry<K, V>>() {
            @Override protected Iterator<Entry<K, V>> delegate() {
              return delegate;
            }
            @Override public Entry<K, V> next() {
              Entry<K, V> ret = super.next();
              return Maps.immutableEntry(ret.getKey(), ret.getValue());
            }
          };
        }

        @Override public Object[] toArray() {
          return standardToArray();
        }
        @Override public <T> T[] toArray(T[] array) {
          return standardToArray(array);
        }
      };
    }
  }

  public static <K, V> ListMultimap<K, V> newArrayListMultimap(Map<Object, Object> map) {
    checkArgument(map.isEmpty());
    //noinspection unchecked
    return Multimaps.newListMultimap((Map) map, (Supplier) ArrayListSupplier.INSTANCE);
  }

  private enum ArrayListSupplier implements Supplier<List<?>> {
    INSTANCE;
    @Override public List<?> get() {
      return Lists.newArrayList();
    }
  }


  // reflections

  public static Field findField(Class<?> clz, String... names) {
    for (String s : names)
      try {
        Field f = clz.getDeclaredField(s);
        f.setAccessible(true);
        return f;
      } catch (Throwable ignored) { }

    throw propagate(new NoSuchFieldException(clz.getName() + ".[" + Joiner.on(", ").join(names) + "]"));
  }

  public static <T> MethodFinder<T> findMethod(Class<T> clz, String... names) {
    return new MethodFinder<>(clz, names);
  }
  public static class MethodFinder<T> {
    private final Class<T> clz;
    private final String[] names;
    private MethodFinder(Class<T> clz, String[] names) {
      this.clz = clz;
      this.names = names;
    }

    public Method withArgs(Class<?>... parameterTypes) {
      for (String s : names)
        try {
          Method m = clz.getDeclaredMethod(s, parameterTypes);
          m.setAccessible(true);
          return m;
        } catch (Throwable ignored) { }

      throw propagate(new NoSuchMethodException(String.format("%s.[%s](%s)",
          clz.getName(),
          Joiner.on(", ").join(names),
          Joiner.on(", ").join(Iterables.transform(Arrays.asList(parameterTypes), ClassNameFunction.INSTANCE))
      )));
    }
    public Invokable<T, Object> asInvokable(Class<?>... parameterTypes) {
      return TypeToken.of(clz).method(withArgs(parameterTypes));
    }
    public MethodInfo asMethodInfo(Class<?>... parameterTypes) {
      return MethodInfo.of(withArgs(parameterTypes));
    }

    private enum ClassNameFunction implements Function<Class<?>, String> {
      INSTANCE;
      @Override public String apply(@Nullable Class<?> input) {
        return input == null ? "null" : input.getName();
      }
    }
  }

  public static <T> T construct(Class<? extends T> clz) {
    try {
      Constructor<? extends T> ctor = clz.getDeclaredConstructor();
      ctor.setAccessible(true);
      return ctor.newInstance();
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      throw propagate(cause != null ? cause : e);
    } catch (Throwable t) {
      throw propagate(t);
    }
  }

  public static <T> T[] newArray(final String componentType, final int length) {
    try {
      //noinspection unchecked
      return (T[]) Array.newInstance(Class.forName(componentType), length);
    }
    catch (Throwable t) {
      throw propagate(t);
    }
  }


  // version validations

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface Modid { }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public @interface Dependency { }

  /**
   * When applied to an outer class, this represents a map from building MC versions to the required MC versions.
   * When applied to an inner class, this represents a map from actual MC versions to the required mod versions.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public @interface Requirements {
    String[] value();
  }

  public static void checkRequirements(Class<?> clz) {
    Set<ArtifactVersion> missing = checkRequirements(clz, clz.getAnnotation(MCVersion.class).value());
    if (!missing.isEmpty()) throw missingMods(missing);
  }

  private static Set<ArtifactVersion> checkRequirements(Class<?> clz, String mcString) {
    Set<ArtifactVersion> missing = Sets.newHashSet();

    ModContainer mc = Loader.instance().getMinecraftModContainer();
    ArtifactVersion m0 = check(clz, "Minecraft", new SimpleVersion(mcString), mc);
    if (m0 != null) missing.add(m0);

    Map<String, ModContainer> mods = Loader.instance().getIndexedModList();
    for (Class<?> c : clz.getDeclaredClasses()) {
      String modid = null;

      for (Field f : c.getDeclaredFields())
        if (f.getAnnotation(Modid.class) != null) {
          checkArgument(modid == null);
          checkArgument(Modifier.isStatic(f.getModifiers()));

          f.setAccessible(true);
          modid = Fields.get(null, f);
        }

      if (modid != null) {
        ArtifactVersion m = check(c, modid, MC_VERSION, mods.get(modid));
        if (m != null) missing.add(m);
      }
    }

    return missing;
  }

  private static ArtifactVersion check(Class<?> c, String modid, SimpleVersion key, ModContainer mc) {
    boolean isLoaded = Loader.isModLoaded(modid);

    if (isLoaded || c.getAnnotation(Dependency.class) != null) {
      Requirements requirements = c.getAnnotation(Requirements.class);

      if (requirements != null) {
        for (String s : requirements.value()) {
          List<String> entry = Splitter.on('=').trimResults().splitToList(s);
          checkArgument(entry.size() == 2);

          if (key.isAtLeast(entry.get(0))) {
            ArtifactVersion r = parse(modid, entry.get(1));

            if (!isLoaded || !r.containsVersion(mc.getProcessedVersion()))
              return r;
            break;
          }
        }
      }

      if (!isLoaded)
        return VersionParser.parseVersionReference(modid);
    }

    return null;
  }

  private static ArtifactVersion parse(String modid, String versionRange) {
    char c = versionRange.charAt(0);
    if (c != '[' && c != '(') versionRange = "[" + versionRange + ",)";
    return VersionParser.parseVersionReference(modid + "@" + versionRange);
  }

  private static MissingModsException missingMods(Set<ArtifactVersion> missingMods) {
    try {
      Constructor<MissingModsException> ctor = MissingModsException.class.getDeclaredConstructor(Set.class);

      ctor.setAccessible(true);
      return ctor.newInstance(missingMods);

    } catch (NoSuchMethodException ignored) {
    } catch (Throwable t) {
      throw propagate(t);
    }

    try {
      Constructor<MissingModsException> ctor =
          MissingModsException.class.getDeclaredConstructor(Set.class, String.class, String.class);

      ctor.setAccessible(true);
      ModContainer mod = Loader.instance().activeModContainer();
      return ctor.newInstance(missingMods, mod.getModId(), mod.getName());

    } catch (Throwable t) {
      throw propagate(t);
    }
  }


  // exceptions

  public static RuntimeException propagate(Throwable t) {
    return propagateWhatever(checkNotNull(t)); // sneaky throw
  }

  private static <T extends Throwable> T propagateWhatever(Throwable t) throws T {
    //noinspection unchecked
    throw (T) t;
  }

}
