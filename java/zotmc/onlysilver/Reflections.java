package zotmc.onlysilver;

import static cpw.mods.fml.common.ObfuscationReflectionHelper.remapFieldNames;
import static cpw.mods.fml.relauncher.ReflectionHelper.findField;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;

public class Reflections {
	
	private static final Invokable<Block, Void>
	DROP_BLOCK_AS_ITEM = findMethod(Block.class, true, of("dropBlockAsItem", "func_149642_a"),
			World.class, int.class, int.class, int.class, ItemStack.class).returning(void.class);
	
	
	public enum Fields {
		INVULNERABLE (Entity.class, true, "invulnerable", "field_83001_bt");
		
		
		private final Field field;
		Fields(Class<?> clz, boolean isObfuscated, String... names) {
			if (isObfuscated)
				names = remapFieldNames(clz.getName(), names);
			
			field = findField(clz, names);
		}
		
		@SuppressWarnings("unchecked")
		public <V> V get(Object instance) {
			try {
				return (V) field.get(instance);
			} catch (Exception e) {
				throw new UnableToAccessFieldException(new String[0], e);
			}
		}
		public <V> void set(Object instance, V value) {
			try {
				field.set(instance, value);
			} catch (Exception e) {
				throw new UnableToAccessFieldException(new String[0], e);
			}
		}
		public <V> void copyValue(Object sourceInstance, Object targetInstance) {
			try {
				field.set(targetInstance, field.get(sourceInstance));
			} catch (Exception e) {
				throw new UnableToAccessFieldException(new String[0], e);
			}
		}
	
	}
	
	
	
	
	public static void dropBlockAsItem(Block block, World world, int x, int y, int z, ItemStack drop) {
		try {
			DROP_BLOCK_AS_ITEM.invoke(block, world, x, y, z, drop);
		} catch (Exception e) {
			throw new UnableToAccessMethodException(e);
		}
	}
	
	

    private static String[] remapMethodNames(String className, String... methodNames) {
        String internalClassName = FMLDeobfuscatingRemapper.INSTANCE.unmap(className.replace('.', '/'));
        String[] mappedNames = new String[methodNames.length];
        int i = 0;
        for (String mName : methodNames)
            mappedNames[i++] = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(internalClassName, mName, null);
        return mappedNames;
    }
    
    private static String[] of(String... varargs) {
    	return varargs;
    }
    
    private static <T> Invokable<T, Object> findMethod(
    		Class<T> clz, boolean isObfuscated, String[] names, Class<?>... methodTypes) {
    	if (isObfuscated)
    		names = remapMethodNames(clz.getName(), names);
    	
    	return TypeToken.of(clz).method(ReflectionHelper.findMethod(clz, null, names, methodTypes));
    }
    
    private static class UnableToAccessMethodException extends RuntimeException {
    	public UnableToAccessMethodException() { }
		public UnableToAccessMethodException(Throwable throwable) {
			super(throwable);
		}
    }
	
}
