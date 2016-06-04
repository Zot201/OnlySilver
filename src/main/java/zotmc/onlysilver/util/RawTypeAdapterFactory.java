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

import java.io.IOException;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javax.annotation.Nullable;

public abstract class RawTypeAdapterFactory<V> implements TypeAdapterFactory {

  protected abstract Class<? super V> targetType();

  protected abstract @Nullable V postProcessing(V in);

  @Override public @Nullable <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    if (type.getRawType() == targetType()) {
      //noinspection unchecked
      final TypeAdapter<V> delegate = gson.getDelegateAdapter(this, (TypeToken<V>) type);

      //noinspection unchecked
      return (TypeAdapter<T>) new TypeAdapter<V>() {
        @Override public void write(JsonWriter out, V value) throws IOException {
          delegate.write(out, value);
        }

        @Override public @Nullable V read(JsonReader in) throws IOException {
          return postProcessing(delegate.read(in));
        }
      };
    }

    return null;
  }


  public static RawTypeAdapterFactory<Set<?>> immutableSet() {
    return new ImmutableSetAdapterFactory();
  }

  private static class ImmutableSetAdapterFactory extends RawTypeAdapterFactory<Set<?>> {
    @Override protected Class<? super Set<?>> targetType() {
      return Set.class;
    }
    @Override protected Set<?> postProcessing(Set<?> in) {
      return ImmutableSet.copyOf(in);
    }
  }

}
