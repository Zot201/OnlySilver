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
package zotmc.onlysilver.util;

import com.google.common.collect.ForwardingMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

public class FluentMultiset<E> extends ForwardingMultiset<E> {

  private final Multiset<E> backing;

  private FluentMultiset(Multiset<E> backing) {
    this.backing = backing;
  }

  @Override protected Multiset<E> delegate() {
    return backing;
  }

  @SafeVarargs public static <E> FluentMultiset<E> of(E... a) {
    return new FluentMultiset<>(ImmutableMultiset.copyOf(a));
  }

  public static <E> FluentMultiset<E> of(E e, int count) {
    return new FluentMultiset<>(ImmutableMultiset.<E>builder().addCopies(e, count).build());
  }

  @SafeVarargs public final FluentMultiset<E> tag(E... a) {
    return new FluentMultiset<>(ImmutableMultiset.<E>builder().addAll(backing).add(a).build());
  }

  public final FluentMultiset<E> tag(E e, int count) {
    return new FluentMultiset<>(ImmutableMultiset.<E>builder().addAll(backing).addCopies(e, count).build());
  }

}
