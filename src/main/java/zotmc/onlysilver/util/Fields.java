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

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Field;

import net.minecraftforge.common.util.EnumHelper;

import javax.annotation.Nullable;

public class Fields {

  public static @Nullable <T> T get(@Nullable Object obj, Field field) {
    try {
      //noinspection unchecked
      return (T) field.get(obj);
    } catch (Throwable t) {
      throw Utils.propagate(t);
    }
  }

  public static <T> void set(@Nullable Object obj, Field field, @Nullable T value) {
    try {
      field.set(obj, value);
    } catch (Throwable t) {
      throw Utils.propagate(t);
    }
  }

  public static <T> void setFinal(@Nullable Object obj, Field field, @Nullable T value) {
    try {
      checkArgument(field.isAccessible());
      EnumHelper.setFailsafeFieldValue(field, obj, value);
    } catch (Throwable t) {
      throw Utils.propagate(t);
    }
  }

}
