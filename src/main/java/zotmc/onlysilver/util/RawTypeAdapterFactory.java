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

  @Override public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    if (type.getRawType() == targetType()) {
      //noinspection unchecked
      final TypeAdapter<V> delegate = gson.getDelegateAdapter(this, (TypeToken<V>) type);

      //noinspection unchecked
      return (TypeAdapter<T>) new TypeAdapter<V>() {
        @Override public void write(JsonWriter out, V value) throws IOException {
          delegate.write(out, value);
        }

        @Override public V read(JsonReader in) throws IOException {
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
