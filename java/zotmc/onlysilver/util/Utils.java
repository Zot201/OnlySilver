package zotmc.onlysilver.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.reflect.Modifier.FINAL;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.MathHelper;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;

import cpw.mods.fml.common.registry.GameData;

public class Utils {
	
	//minecraft and forge
	
	public static class EntityLists {
		@SuppressWarnings("unchecked")
		public static Map<String, Class<? extends Entity>> stringToClassMapping() {
			return EntityList.stringToClassMapping;
		}
	}
	
	public static class CraftingManagers {
		@SuppressWarnings("unchecked")
		public static List<IRecipe> getRecipeList() {
			return CraftingManager.getInstance().getRecipeList();
		}
	}
	
	public static String getEntityString(Class<? extends Entity> clz) {
		return (String) EntityList.classToStringMapping.get(clz);
	}
	
	public static Optional<Item> getItem(String modid, String name) {
		return Optional.fromNullable(GameData.getItemRegistry().getRaw(modid + ":" + name));
	}
	
	public static Optional<Block> getBlock(String modid, String name) {
		return Optional.fromNullable(GameData.getBlockRegistry().getRaw(modid + ":" + name));
	}
	
	
	
	//maths
	
	public static final float PI = (float) Math.PI;
	
	public static int floor(double a) {
		return MathHelper.floor_double(a);
	}
	public static int floor(float a) {
		return MathHelper.floor_float(a);
	}
	
	
	
	//reflections and asm
	
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getClassChecked(String className) throws ClassNotFoundException {
		return (Class<T>) Class.forName(className);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> TypeToken<T> upcast(Class<? extends T> type) {
		return (TypeToken<T>) TypeToken.of(type);
	}
	
	public static <T, R> Uncheck<T, R> uncheck(final Invokable<T, R> invokable) {
		return new Uncheck<T, R>() {
			@Override public R invoke(T receiver, Object... args) {
				try {
					return invokable.invoke(receiver, args);
				} catch (Throwable e) {
					throw Throwables.propagate(e);
				}
			}
		};
	}
	public static abstract class Uncheck<T, R> {
		private Uncheck() { }
		public abstract R invoke(T receiver, Object... args);
	}
	
	public static Supplier<?> newArray(final String componentType, final int length) {
		return new Supplier<Object>() {
			@Override public Object get() {
				try {
					return Array.newInstance(Class.forName(componentType), length);
				} catch (Throwable e) {
					throw Throwables.propagate(e);
				}
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(Field field, Object obj) {
		try {
			return (T) field.get(obj);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	public static <T> void set(Field field, Object obj, T value) {
		try {
			field.set(obj, value);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	public static Field definalize(Field field) {
		try {
			MODIFIERS.setInt(field, field.getModifiers() & ~FINAL);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
		return field;
	}
	private static final Field MODIFIERS;
	static {
		Field f = null;
		try {
			f = Field.class.getDeclaredField("modifiers");
			f.setAccessible(true);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
		MODIFIERS = f;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Class<T> defineClass(String name, byte[] data) {
		return (Class<T>) ASMClassLoader.INSTANCE.define(name, data);
	}
	private static class ASMClassLoader extends ClassLoader {
		private static final ASMClassLoader INSTANCE = new ASMClassLoader();
		private ASMClassLoader() {
			super(ASMClassLoader.class.getClassLoader());
		}
		public Class<?> define(String name, byte[] data) {
			return defineClass(name, data, 0, data.length);
		}
	}
	
	
	
	//functional idioms
	
	public static Runnable conditional(final Supplier<Boolean> condition, final Runnable runnable) {
		return new Runnable() {
			@Override public void run() {
				if (condition.get())
					runnable.run();
			}
		};
	}
	
	public static <T> Consumer<T> conditional(final Supplier<Boolean> condition, final Consumer<T> consumer) {
		return new Consumer<T>() {
			@Override public void accept(T t) {
				if (condition.get())
					consumer.accept(t);
			}
		};
	}
	
	public static Function<String, Integer> parseInteger() {
		return ParseInt.INSTANCE;
	}
	private enum ParseInt implements Function<String, Integer> {
		INSTANCE;
		@Override public Integer apply(String input) {
			return Integer.parseInt(input);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Function<T, Supplier<T>> supplierWrap() {
		return (Function) SupplierWrap.INSTANCE;
	}
	private enum SupplierWrap implements Function<Object, Supplier<?>> {
		INSTANCE;
		@Override public Supplier<?> apply(Object input) {
			return Suppliers.ofInstance(input);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Supplier<T> nullSupplier() {
		return (Supplier<T>) NullSupplier.INSTANCE;
	}
	private enum NullSupplier implements Supplier<Object> {
		INSTANCE;
		@Override public Object get() {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Consumer<T> nullConsumer() {
		return (Consumer<T>) NullConsumer.INSTANCE;
	}
	private enum NullConsumer implements Consumer<Object> {
		INSTANCE;
		@Override public void accept(Object t) { }
	}
	
	@SuppressWarnings("unchecked")
	public static <F, T> Function<F, T> constant(T value) {
		return (Function<F, T>) Functions.constant(value);
	}
	
	
	
	//others
	
	public static <T> Feature<T> featureOf(final T instance) {
		checkNotNull(instance);
		
		return new Feature<T>() {
			@Override public T get() {
				return instance;
			}
			@Override public boolean exists() {
				return true;
			}
		};
	}
	
}
