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

import com.google.common.base.Optional;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class Reserve<T> implements Feature<T> {

  private T reference;
  private Reserve() { }

  public static <T> Reserve<T> absent() {
    return new Reserve<>();
  }


  @Override public boolean exists() {
    return reference != null;
  }

  @Override public T get() {
    checkState(exists());
    return reference;
  }

  @SuppressWarnings("WeakerAccess")
  public @Nullable T orNull() {
    return reference;
  }

  public Reserve<T> set(T reference) {
    checkState(!exists());
    this.reference = checkNotNull(reference);
    return this;
  }

  @SuppressWarnings("Guava")
  public Optional<T> toOptional() {
    return Optional.fromNullable(orNull());
  }

}
