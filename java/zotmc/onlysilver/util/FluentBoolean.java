package zotmc.onlysilver.util;

import com.google.common.base.Supplier;

import cpw.mods.fml.common.Loader;

public abstract class FluentBoolean implements Supplier<Boolean> {
	
	public FluentBoolean and(final Supplier<Boolean> other) {
		return new FluentBoolean() {
			@Override public Boolean get() {
				return FluentBoolean.this.get() && other.get();
			}
		};
	}
	
	
	public static FluentBoolean alwaysTrue() {
		return ALWAYS_TRUE;
	}
	private static final FluentBoolean ALWAYS_TRUE = new FluentBoolean() {
		@Override public Boolean get() {
			return true;
		}
	};
	
	public static FluentBoolean isModLoaded(final String modid) {
		return new FluentBoolean() {
			@Override public Boolean get() {
				return Loader.isModLoaded(modid);
			}
		};
	}
	
}
