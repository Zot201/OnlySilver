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
package zotmc.onlysilver.config;

import java.util.Objects;

import zotmc.onlysilver.util.JsonHelper;

import com.google.common.base.Supplier;

import javax.annotation.Nullable;

public final class GenDefaults implements Supplier<JsonHelper> {

  final String dimensions; // nullable
  final int size, count, minHeight, maxHeight;

  GenDefaults(@Nullable String dimensions, int size, int count, int minHeight, int maxHeight) {
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
