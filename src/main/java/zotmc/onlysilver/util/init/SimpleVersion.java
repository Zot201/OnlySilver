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
package zotmc.onlysilver.util.init;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import javax.annotation.Nonnull;

public final class SimpleVersion implements Comparable<SimpleVersion> {

  private static final Ordering<Iterable<Integer>>
  ORDER = Ordering.<Integer>natural().lexicographical();

  private final ImmutableList<Integer> parts;

  public SimpleVersion(String s) {
    this.parts = parse(s).toList();
  }

  @SuppressWarnings("Guava")
  private static FluentIterable<Integer> parse(String s) {
    return FluentIterable
        .from(Splitter.on('.').split(s))
        .transform(IntegerParser.INSTANCE);
  }

  private int compareTo(Iterable<Integer> parts) {
    return ORDER.compare(this.parts, parts);
  }
  @Override public int compareTo(@Nonnull SimpleVersion version) {
    return compareTo(version.parts);
  }

  public boolean isAtLeast(String version) {
    return compareTo(parse(version)) >= 0;
  }

  public boolean isBelow(String version) {
    return compareTo(parse(version)) < 0;
  }

  @Override public int hashCode() {
    return parts.hashCode();
  }

  @Override public boolean equals(Object obj) {
    return obj == this || obj instanceof SimpleVersion && parts.equals(((SimpleVersion) obj).parts);
  }

  @Override public String toString() {
    return Joiner.on('.').join(parts);
  }

  private enum IntegerParser implements Function<String, Integer> {
    INSTANCE;
    @Override public Integer apply(String input) {
      return Integer.parseInt(input);
    }
  }

}
