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


  @SuppressWarnings("WeakerAccess")
  public static class WorldPredicate extends CacheLoader<World, Boolean> implements Predicate<World> {
    public final String regex;
    private final LoadingCache<World, Boolean> cache = CacheBuilder.newBuilder().weakKeys().build(this);
    WorldPredicate(String regex) {
      this.regex = regex;
    }

    @Deprecated @Override public Boolean load(@SuppressWarnings("NullableProblems") World key) {
      try {
        // TODO: Find alternative way to identify mod dimensions (DimensionType is enum now...)
        return key.provider.getDimensionType().getName().matches(regex);
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
