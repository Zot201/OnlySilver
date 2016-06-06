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

import com.google.common.base.Supplier;
import zotmc.onlysilver.config.AbstractConfig;
import zotmc.onlysilver.config.AbstractConfig.Property;
import zotmc.onlysilver.data.LangData;
import zotmc.onlysilver.util.Utils.Localization;

import javax.annotation.Nullable;

public abstract class AbstractBooleanRow<T extends AbstractConfig<T>> extends WidgetRow {

  private final ButtonComponent comp = new ButtonComponent();

  @Override protected Widget<?> widget() {
    return comp.button;
  }

  protected abstract T getTemp();

  protected abstract Property<Boolean> toProperty(T t);

  protected abstract @Nullable Boolean getRawValue(Property<Boolean> p);

  protected abstract void setRawValue(Property<Boolean> p, @Nullable Boolean v);


  private class ButtonComponent implements Runnable, Supplier<String> {
    final Button button = new Button(this, this);

    @Override public void run() {
      Property<Boolean> p = toProperty(getTemp());
      setRawValue(p, getRawValue(p) != null ? null : !p.get());
    }
    @Override public String get() {
      Property<Boolean> p = toProperty(getTemp());
      return State.getLocalized(getRawValue(p), p);
    }
  }

  private enum State {
    DEFAULT (null, LangData.DEFAULT),
    ON (true, LangData.ON),
    OFF (false, LangData.OFF);

    final Boolean value;
    private final Localization localization;
    State(Boolean value, Localization localization) {
      this.value = value;
      this.localization = localization;
    }
    static State of(@Nullable Boolean value) {
      return value == null ? DEFAULT : value ? ON : OFF;
    }

    public static String getLocalized(@Nullable Boolean raw, Property<Boolean> p) {
      return raw != null ? (raw ? ON : OFF).localization.toString()
          : DEFAULT.localization.toString((p.get() ? ON : OFF).localization);
    }
  }

}
