package zotmc.onlysilver.config;

import java.util.Objects;

import zotmc.onlysilver.util.JsonHelper;

import com.google.common.base.Supplier;

public final class GenDefaults implements Supplier<JsonHelper> {

  public final String dimensions; // nullable
  public final int size, count, minHeight, maxHeight;

  public GenDefaults(String dimensions, int size, int count, int minHeight, int maxHeight) {
    this.dimensions = dimensions;
    this.size = size;
    this.count = count;
    this.minHeight = minHeight;
    this.maxHeight = maxHeight;
  }

  @Override public JsonHelper get() {
    return new JsonHelper()
      .set("dimensions", dimensions)
      .set("size", size)
      .set("count", count)
      .set("minHeight", minHeight)
      .set("maxHeight", maxHeight);
  }

  @Override public int hashCode() {
    return Objects.hash(dimensions, size, count, minHeight, maxHeight);
  }

  @Override public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj instanceof GenDefaults) {
      GenDefaults o = (GenDefaults) obj;
      return Objects.equals(dimensions, o.dimensions)
          && size == o.size && count == o.count && minHeight == o.minHeight && maxHeight == o.maxHeight;
    }
    return false;
  }

}
