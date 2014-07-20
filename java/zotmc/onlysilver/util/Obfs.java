package zotmc.onlysilver.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static cpw.mods.fml.common.ObfuscationReflectionHelper.remapFieldNames;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class Obfs {
	
	public static Field findField(Class<?> clz, String... names) {
		Field f = null;
		for (String s : remapFieldNames(clz.getName(), names))
			try {
				f = clz.getDeclaredField(s);
				f.setAccessible(true);
				break;
			} catch (Throwable ignored) { }
		
		return checkNotNull(f);
	}
	
	public static Field findFieldFinal(Class<?> clz, String... names) {
		return Utils.definalize(findField(clz, names));
	}
	
	
	
	public static <T> MethodFinder<T> findMethod(Class<T> clz, String... names) {
		return new MethodFinder<T>(clz, names);
	}
	
	public static class MethodFinder<T> {
		private final Class<T> clz;
		private final String[] names;
		private MethodFinder(Class<T> clz, String[] names) {
			this.clz = clz;
			this.names = names;
		}
		public Method withArgs(Class<?>... parameterTypes) {
			Method m = null;
			for (String s : remapMethodNames(clz.getName(), names))
				try {
					m = clz.getDeclaredMethod(s, parameterTypes);
					m.setAccessible(true);
					break;
				} catch (Throwable ignored) { }
			
			return m;
		}
		public MethodInfo asMethodInfo(Class<?>... parameterTypes) {
			return MethodInfo.of(withArgs(parameterTypes));
		}
		public Invokable<T, Object> asInvokable(Class<?>... parameterTypes) {
			return TypeToken.of(clz).method(withArgs(parameterTypes));
		}
	}
	
    private static String[] remapMethodNames(String className, String... methodNames) {
        String internalClassName = FMLDeobfuscatingRemapper.INSTANCE.unmap(className.replace('.', '/'));
        String[] mappedNames = new String[methodNames.length];
        int i = 0;
        for (String mName : methodNames)
            mappedNames[i++] = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(
            		internalClassName, mName, null);
        return mappedNames;
    }

}
