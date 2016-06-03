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

import java.util.List;

import zotmc.onlysilver.config.AbstractConfig;
import zotmc.onlysilver.config.AbstractConfig.Property;

public abstract class ValueInjectionRow<T extends AbstractConfig<T>, V> extends ScreenFactoryRow {

  // value
  protected abstract void injectValue(V v);
  protected abstract V toImmutable();

  // config
  protected abstract T getTemp();

  protected abstract Property<V> toProperty(T t);
  protected abstract void setValue(Property<V> p, V v);

  @Override protected void loadValue() {
    injectValue(toProperty(getTemp()).get());
  }
  @Override protected void resetValue() {
    injectValue(toProperty(getTemp()).base().get());
  }
  @Override protected void saveValue() {
    setValue(toProperty(getTemp()), toImmutable());
  }

  // row
  public abstract Iterable<? extends Row> getUpperRows(int w, Holder<List<String>> hoveringText, V defaultValue);

  @Override public Iterable<? extends Row> getUpperRows(int w, Holder<List<String>> hoveringText) {
    return getUpperRows(w, hoveringText, toProperty(getTemp()).base().get());
  }

}
