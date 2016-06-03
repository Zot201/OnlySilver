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
package zotmc.onlysilver.config.gui;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

class PseudoIterator<E> implements Iterable<E> {

  private final Iterable<E> iterable;
  private final E paddingElement;

  private Iterator<E> backing;
  private int currentIndex;
  private E lastElement;

  private PseudoIterator(Iterable<E> iterable, E paddingElement) {
    this.iterable = iterable;
    this.paddingElement = paddingElement;
    backing = iterable.iterator();
  }

  public static <E> PseudoIterator<E> of(Iterable<E> iterable, E paddingElement, int padding) {
    if (padding != 0) iterable = Iterables.concat(iterable, Collections.nCopies(padding, paddingElement));
    return new PseudoIterator<>(iterable, paddingElement);
  }


  public E next(int index) {
    if (index == currentIndex - 1)
      return lastElement;

    try {
      if (index > currentIndex)
        Iterators.advance(backing, index - currentIndex);
      else if (index < currentIndex) {
        backing = iterable.iterator();
        Iterators.advance(backing, index);
      }

      currentIndex = index + 1;
      return lastElement = backing.next();

    } catch (NoSuchElementException ignored) { }

    return paddingElement;
  }

  public int size() {
    return Iterables.size(iterable);
  }

  @Override public Iterator<E> iterator() {
    return Iterators.unmodifiableIterator(iterable.iterator());
  }

}
