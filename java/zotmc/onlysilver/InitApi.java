package zotmc.onlysilver;

import java.lang.reflect.Field;

import zotmc.onlysilver.api.OnlySilverAPI;
import zotmc.onlysilver.item.Instrumentum;
import zotmc.onlysilver.util.Feature;
import zotmc.onlysilver.util.Utils;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;

class InitApi {
	
	private static void init() {
		for (Field f : OnlySilverAPI.class.getDeclaredFields())
			try {
				Feature<?> feature = getFeature(f.getName());
				
				if (feature.exists())
					Utils.definalize(f).set(null, Optional.of(feature.get()));
				
			} catch (Throwable e) {
				throw Throwables.propagate(e);
			}
		
	}
	
	private static Feature<?> getFeature(String name) throws Throwable {
		try {
			return (Feature<?>) Instrumentum.class.getDeclaredField(name).get(null);
		} catch (Throwable ignored) {
			return (Feature<?>) Contents.class.getDeclaredField(name).get(null);
		}
	}

}
