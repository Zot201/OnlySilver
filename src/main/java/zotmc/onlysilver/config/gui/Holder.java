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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Holder<T> {

  private T reference;
  private Holder() { }

  public static <T> Holder<T> absent() {
    return new Holder<>();
  }
  public static <T> Holder<T> of(T reference) {
    Holder<T> ret = absent();
    ret.set(reference);
    return ret;
  }
  public static <T> Holder<T> ofNullable(T reference) {
    Holder<T> ret = absent();
    ret.setNullable(reference);
    return ret;
  }

  public void set(T reference) {
    this.reference = checkNotNull(reference);
  }
  public void clear() {
    reference = null;
  }
  public void setNullable(T reference) {
    this.reference = reference;
  }

  public T get() {
    checkState(reference != null);
    return reference;
  }
  public T orNull() {
    return reference;
  }
  public T or(T defaultValue) {
    checkNotNull(defaultValue);
    return reference != null ? reference : defaultValue;
  }

  public boolean isPresent() {
    return reference != null;
  }

}
