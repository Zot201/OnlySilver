package zotmc.onlysilver.util;

import com.google.common.base.Supplier;

import cpw.mods.fml.common.Loader;

public abstract class BooleanSupplier implements Supplier<Boolean> {
	
	public BooleanSupplier and(final Supplier<Boolean> other) {
		return new BooleanSupplier() {
			@Override public Boolean get() {
				return BooleanSupplier.this.get() && other.get();
			}
		};
	}
	
	
	public static BooleanSupplier alwaysTrue() {
		return ALWAYS_TRUE;
	}
	private static final BooleanSupplier ALWAYS_TRUE = new BooleanSupplier() {
		@Override public Boolean get() {
			return true;
		}
	};
	
	public static BooleanSupplier isModLoaded(final String modid) {
		return new BooleanSupplier() {
			@Override public Boolean get() {
				return Loader.isModLoaded(modid);
			}
		};
	}
	
}
