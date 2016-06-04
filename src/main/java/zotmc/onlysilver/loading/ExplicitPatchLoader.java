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
package zotmc.onlysilver.loading;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Iterator;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import zotmc.onlysilver.loading.Patcher.ClientOnly;

import com.google.common.base.Throwables;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;

class ExplicitPatchLoader implements Iterable<Patcher> {

  private final Class<?> clz;

  ExplicitPatchLoader(Class<?> clz) {
    this.clz = checkNotNull(clz);
  }

  private static boolean sideEnabled(AnnotatedElement obj) {
    return FMLLaunchHandler.side().isClient() || obj.getAnnotation(ClientOnly.class) == null;
  }

  @Override public Iterator<Patcher> iterator() {
    if (!sideEnabled(clz)) return Iterators.emptyIterator();

    return new AbstractIterator<Patcher>() {
      final Iterator<Class<?>> classes = Iterators.forArray(clz.getDeclaredClasses());
      Iterator<Field> fields = Iterators.emptyIterator();

      @Override protected Patcher computeNext() {
        while (true) {
          if (fields.hasNext()) {
            Field f = fields.next();
            if (!Patcher.class.isAssignableFrom(f.getType()) || !sideEnabled(f)) continue;

            try {
              f.setAccessible(true);
              return (Patcher) f.get(null);
            } catch (Throwable t) {
              throw Throwables.propagate(t);
            }
          }

          if (classes.hasNext()) {
            Class<?> clz = classes.next();
            if (!sideEnabled(clz)) continue;

            fields = Iterators.forArray(clz.getDeclaredFields());
            continue;
          }

          return endOfData();
        }
      }
    };
  }

}
