package zotmc.onlysilver.oregen;

import java.util.regex.PatternSyntaxException;

import net.minecraft.world.World;
import zotmc.onlysilver.util.JsonHelper;

import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import javax.annotation.Nullable;

public class ExtSettings {

  public static final WorldPredicate DEFAULT_DIMS = new WorldPredicate("(?!^Nether$|^The End$)(?s).*");

  final WorldPredicate silverDimensions;
  final int silverSize;
  final int silverCount;
  final int silverMinHeight;
  final int silverMaxHeight;

  ExtSettings(JsonHelper factory) {
    String s = factory.getAsString("dimensions", null);
    silverDimensions = s != null ? new WorldPredicate(s) : DEFAULT_DIMS;
    silverSize = factory.getAsInt("size");
    silverCount = factory.getAsInt("count");
    silverMinHeight = factory.getAsInt("minHeight");
    silverMaxHeight = factory.getAsInt("maxHeight");
  }


  private static class WorldPredicate extends CacheLoader<World, Boolean> implements Predicate<World> {
    public final String regex;
    private final LoadingCache<World, Boolean> cache = CacheBuilder.newBuilder().weakKeys().build(this);
    WorldPredicate(String regex) {
      this.regex = regex;
    }

    @Deprecated @Override public Boolean load(@SuppressWarnings("NullableProblems") World key) {
      try {
        return key.getChunkProvider().makeString().matches(regex); // TODO: Check if this work with DEFAULT_DIMS
      }
      catch (PatternSyntaxException e) {
        return true;
      }
    }
    @Override public boolean apply(World input) {
      return cache.getUnchecked(input);
    }
  }

}
