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

import zotmc.onlysilver.config.gui.Slider.Slidable;

public abstract class SliderRow extends WidgetRow implements Slidable {

  private final Slider slider = new Slider(this);

  @Override protected Widget<?> widget() {
    return slider;
  }

  @Override protected int widgetPos(int k) {
    return k * 11 / 21;
  }

}
