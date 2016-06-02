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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import zotmc.onlysilver.data.ModData.OnlySilvers;
import zotmc.onlysilver.util.Klas.KlastWriter;
import zotmc.onlysilver.util.init.MethodInfo;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.reflect.Invokable;

@SuppressWarnings({"Guava", "WeakerAccess"})
public class Dynamic {
  
  private static final String MODID = OnlySilvers.MODID;
  
  public static <T> Refer<T> refer(Class<?> clz, String field) {
    return new Refer<>(Klas.ofClass(clz), Null.INSTANCE, field);
  }
  public static <T> Refer<T> refer(String clz, String field) {
    return new Refer<>(Klas.ofName(clz), Null.INSTANCE, field);
  }
  
  public static <U> Invoke<U> invoke(Class<?> clz, String method) {
    return new Invoke<>(Klas.ofClass(clz), Null.INSTANCE, method);
  }
  public static <U> Invoke<U> invoke(String clz, String method) {
    return new Invoke<>(Klas.ofName(clz), Null.INSTANCE, method);
  }
  
  public static <T> Construct<T> construct(Class<? extends T> clz) {
    return new Construct<>(Klas.ofClass(clz));
  }
  public static <T> Construct<T> construct(final String clz) {
    return new Construct<>(Klas.ofName(clz));
  }
  
  
  private static class TypeArg<T> {
    final Klas<T> type;
    final T arg;
    private TypeArg(Klas<T> type, T arg) {
      this.type = type;
      this.arg = arg;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static TypeArg<?> of(Object obj) {
      return new TypeArg(Klas.ofClass(obj.getClass()), obj);
    }
  }
  
  public static abstract class Chain<T> implements Supplier<T> {
    private Chain() { }
    
    public <U> Refer<U> refer(String field) {
      return new Refer<>(null, this, field);
    }
    public <U> Invoke<U> invoke(String method) {
      return new Invoke<>(null, this, method);
    }
    
    public <U> Chain<T> assign(final String field, final U value) {
      return new Chain<T>() { public T get() {
        try {
          T ret = Chain.this.get();
          ret.getClass().getField(field).set(ret, value);
          return ret;
        } catch (Throwable e) {
          throw Utils.propagate(e);
        }
      }};
    }
    public Chain<T> call(final Invokable<T, ?> method, final Object... args) {
      return new Chain<T>() { public T get() {
        try {
          T ret = Chain.this.get();
          method.invoke(ret, args);
          return ret;
        } catch (Throwable e) {
          throw Utils.propagate(e);
        }
      }};
    }
  }

  public static abstract class Viable<T, U extends Viable<T, U>> extends Chain<T> {
    final ImmutableList<Supplier<TypeArg<?>>> args;
    
    private Viable(ImmutableList<Supplier<TypeArg<?>>> args) {
      this.args = args;
    }
    protected abstract U derive(ImmutableList<Supplier<TypeArg<?>>> args);
    
    U viaArg(Supplier<TypeArg<?>> arg) {
      return derive(ImmutableList.<Supplier<TypeArg<?>>>builder()
          .addAll(args)
          .add(arg)
          .build());
    }
    
    <V> U viaKlas(final Klas<V> type, final Supplier<V> arg) {
      return viaArg(() -> new TypeArg<V>(type, arg.get()));
    }
    public <V> U via(Class<V> type, Supplier<V> arg) {
      return viaKlas(Klas.ofClass(type), arg);
    }
    public <V> U via(Class<V> type, V arg) {
      return via(type, Suppliers.ofInstance(arg));
    }
    public <V> U via(String type, Supplier<V> arg) {
      return viaKlas(Klas.ofName(type), arg);
    }
    
    public <V> U via(final Supplier<V> arg) {
      return viaArg(() -> TypeArg.of(arg.get()));
    }
    public <V> U via(V arg) {
      return via(Suppliers.ofInstance(arg));
    }
    
    public U viaInt(final Supplier<Integer> arg) {
      return viaArg(() -> new TypeArg<>(Klas.INT, arg.get()));
    }
    public U viaInt(int arg) {
      return viaInt(Suppliers.ofInstance(arg));
    }
  }
  
  public static class Refer<T> extends Chain<T> {
    private final Klas<?> clz;
    private final Supplier<?> obj;
    private final String field;
    
    private Refer(Klas<?> clz, Supplier<?> obj, String field) {
      this.clz = clz;
      this.obj = obj;
      this.field = field;
    }
    
    @SuppressWarnings("unchecked")
    @Override public T get() {
      try {
        if (clz != null) {
          Field f = clz.toClass().getDeclaredField(field);
          f.setAccessible(true);
          return (T) f.get(obj.get());
        }
        
        Object o = obj.get();
        return (T) o.getClass().getField(field).get(o);
        
      } catch (Throwable e) {
        throw Utils.propagate(e);
      }
    }
  }
  
  public static class Invoke<T> extends Viable<T, Invoke<T>> {
    private final Klas<?> clz;
    private final Supplier<?> obj;
    private final String method;
    
    private Invoke(Klas<?> clz, Supplier<?> obj, String method,
        ImmutableList<Supplier<TypeArg<?>>> args) {
      super(args);
      this.clz = clz;
      this.obj = obj;
      this.method = method;
    }
    private Invoke(Klas<?> clz, Supplier<?> obj, String method) {
      this(clz, obj, method, ImmutableList.of());
    }
    @Override protected Invoke<T> derive(ImmutableList<Supplier<TypeArg<?>>> args) {
      return new Invoke<T>(clz, obj, method, args);
    }
    
    @SuppressWarnings("unchecked")
    @Override public T get() {
      try {
        List<Class<?>> types = Lists.newArrayList();
        List<Object> args = Lists.newArrayList();
        
        for (Supplier<TypeArg<?>> supplier : this.args) {
          TypeArg<?> p = supplier.get();
          types.add(p.type.toClass());
          args.add(p.arg);
        }
        
        if (clz != null) {
          Method m = clz.toClass().getDeclaredMethod(method, Iterables.toArray(types, Class.class));
          m.setAccessible(true);
          return (T) m.invoke(obj.get(), args.toArray());
        }
        
        Object o = obj.get();
        return (T) o.getClass()
            .getMethod(method, Iterables.toArray(types, Class.class))
            .invoke(o, args.toArray());
        
      } catch (Throwable e) {
        throw Utils.propagate(e);
      }
    }
  }
  
  public static class Construct<T> extends Viable<T, Construct<T>> {
    private final Klas<? extends T> clz;
    
    private Construct(Klas<? extends T> clz, ImmutableList<Supplier<TypeArg<?>>> args) {
      super(args);
      this.clz = clz;
    }
    private Construct(Klas<? extends T> clz) {
      this(clz, ImmutableList.of());
    }
    @Override protected Construct<T> derive(ImmutableList<Supplier<TypeArg<?>>> args) {
      return new Construct<>(clz, args);
    }
    
    @Override public T get() {
      try {
        List<Class<?>> types = Lists.newArrayList();
        List<Object> args = Lists.newArrayList();
        
        for (Supplier<TypeArg<?>> supplier : this.args) {
          TypeArg<?> p = supplier.get();
          types.add(p.type.toClass());
          args.add(p.arg);
        }
        
        Constructor<? extends T> c =
            clz.toClass().getDeclaredConstructor(Iterables.toArray(types, Class.class));
        c.setAccessible(true);
        return c.newInstance(args.toArray());
        
      } catch (Throwable e) {
        throw Utils.propagate(e);
      }
    }


    public Extend<T> extend(Consumer<GeneratorAdapter> extend) {
      return new Extend<>(this, extend, ImmutableList.of());
    }
    public Extend<T> extend() {
      return extend(EmptyConsumer.INSTANCE);
    }
    
    public Extend<T> assemble(Consumer<KlastWriter<?>> assemble) {
      return extend().assemble(assemble);
    }
    
    private enum EmptyConsumer implements Consumer<GeneratorAdapter> {
      INSTANCE;
      @Override public void accept(GeneratorAdapter t) { }
    }
  }
  
  public static class Extend<T> extends Chain<T> {
    private static int nextId;
    private final Construct<T> parent;
    private final Consumer<GeneratorAdapter> extend;
    private final ImmutableList<Consumer<KlastWriter<?>>> assembles;
    
    private Extend(Construct<T> parent, Consumer<GeneratorAdapter> extend,
        ImmutableList<Consumer<KlastWriter<?>>> assembles) {
      this.parent = parent;
      this.extend = extend;
      this.assembles = assembles;
    }
    
    public Extend<T> assemble(Consumer<KlastWriter<?>> assemble) {
      return new Extend<>(parent, extend,
          ImmutableList.<Consumer<KlastWriter<?>>>builder()
              .addAll(assembles)
              .add(assemble)
              .build()
      );
    }
    
    String getUniqueName() {
      return String.format("%s_ASM_%d_%s", MODID, nextId++, parent.clz.toClass().getSimpleName());
    }
    
    @Override public T get() {
      List<TypeArg<?>> args = FluentIterable.from(parent.args)
          .transform(Suppliers.supplierFunction())
          .toList();
      
      KlastWriter<? extends T> cw = new KlastWriter<>(getUniqueName(), parent.clz);
      cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER);
      cw.visitSource(".dynamic", null);
      
      MethodInfo m = MethodInfo.of("<init>", Type.VOID_TYPE,
          FluentIterable.from(args).transform(TypeAdapter.INSTANCE).toArray(Type.class));
      GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null, null, cw);
      mg.loadThis();
      mg.loadArgs();
      mg.invokeConstructor(parent.clz.toType(), m);
      extend.accept(mg);
      mg.returnValue();
      mg.endMethod();
      
      for (Consumer<KlastWriter<?>> assemble : assembles)
        assemble.accept(cw);
      
      return new Construct<>(cw.define(), FluentIterable.from(args).transform(Wrap.TYPE_ARG).toList()).get();
    }
  }
  
  
  private enum Null implements Supplier<Object> {
    INSTANCE;
    @Override public Object get() { return null; }
  }
  
  private enum TypeAdapter implements Function<TypeArg<?>, Type> {
    INSTANCE;
    @Override public Type apply(TypeArg<?> input) {
      return input.type.toType();
    }
  }
  
  private static class Wrap<T> implements Function<T, Supplier<T>> {
    static final Wrap<TypeArg<?>> TYPE_ARG = new Wrap<>();
    @Override public Supplier<T> apply(T input) {
      return Suppliers.ofInstance(input);
    }
  }
  
}
