package zotmc.onlysilver.oregen;

import java.util.regex.PatternSyntaxException;

import net.minecraft.world.World;
import zotmc.onlysilver.util.JsonHelper;

import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class ExtSettings {

	public static final WorldPredicate DEFAULT_DIMS = new WorldPredicate("(?!^Nether$|^The End$)(?s).*");
	
	public final WorldPredicate silverDimensions;
	public final int silverSize;
	public final int silverCount;
	public final int silverMinHeight;
	public final int silverMaxHeight;
	
	public ExtSettings(JsonHelper factory) {
		String s = factory.getAsString("dimensions", null);
		silverDimensions = s != null ? new WorldPredicate(s) : DEFAULT_DIMS;
		silverSize = factory.getAsInt("size");
		silverCount = factory.getAsInt("count");
		silverMinHeight = factory.getAsInt("minHeight");
		silverMaxHeight = factory.getAsInt("maxHeight");
	}
	
	
	public static class WorldPredicate extends CacheLoader<World, Boolean> implements Predicate<World> {
		public final String regex;
		private final LoadingCache<World, Boolean> cache = CacheBuilder.newBuilder().weakKeys().build(this);
		public WorldPredicate(String regex) {
			this.regex = regex;
		}
		
		@Deprecated @Override public Boolean load(World key) {
			try {
				return key.provider.getDimensionName().matches(regex);
			} catch (PatternSyntaxException e) {
				return true;
			}
		}
		@Override public boolean apply(World input) {
			return cache.getUnchecked(input);
		}
	}
	
}
