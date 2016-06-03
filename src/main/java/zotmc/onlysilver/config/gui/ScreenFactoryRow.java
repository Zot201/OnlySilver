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
import java.util.List;

import zotmc.onlysilver.data.LangData;

abstract class ScreenFactoryRow extends WidgetRow {

  private final Button button = new Button(new EditScreen(), LangData.EDIT);

  // widget
  @Override protected Widget<?> widget() {
    return button;
  }

  // screen
  protected abstract void loadValue();
  protected abstract void resetValue();
  protected abstract void saveValue();

  public int getRowHeight() {
    return 20;
  }

  public abstract Iterable<? extends Row> getUpperRows(int w, Holder<List<String>> hoveringText);

  protected Iterable<Row> getLowerRows() {
    return Collections.<Row>nCopies(2, EmptyRow.INSTANCE);
  }


  private class EditScreen extends AbstractConfigScreen implements Runnable {

    @Override public void create() {
      loadValue();
    }
    @Override public void destroy() {
      loadValue();
    }
    @Override protected void reset() {
      resetValue();
    }
    @Override protected void save() {
      saveValue();
    }

    @Override protected Element getTitleElement(int w) {
      Icon<?> icon = icon();
      return icon != null ? icon.title(title(), w / 2, 16) : new Title(title(), w / 2, 16);
    }

    @Override public int getRowHeight() {
      return ScreenFactoryRow.this.getRowHeight();
    }
    @Override public Iterable<? extends Row> getUpperRows(int w, Holder<List<String>> hoveringText) {
      return ScreenFactoryRow.this.getUpperRows(w, hoveringText);
    }
    @Override protected Iterable<Row> getLowerRows() {
      return ScreenFactoryRow.this.getLowerRows();
    }

    @Override public void run() {
      GuiScreenWrapper.display(this);
    }
  }

}
