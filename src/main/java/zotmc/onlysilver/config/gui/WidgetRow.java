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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.google.common.base.Supplier;

import javax.annotation.Nullable;

@SuppressWarnings("Guava")
abstract class WidgetRow implements Row {

  // widget
  protected abstract Widget<?> widget();

  protected int widgetPos(int k) {
    return k * 7 / 12;
  }

  // title
  protected @Nullable Icon<?> icon() {
    return null;
  }

  protected abstract Supplier<String> title();


  // row
  @Override public void drawRow(int x, int y, int w, int h, int mouseX, int mouseY) {
    int l = widgetPos(w + 144) - 72;
    widget().setLeftTop(x + l, y - 1)
      .setWidthHeight(w - l, h + 3)
      .draw(mouseX, mouseY);

    drawTitle(Minecraft.getMinecraft().fontRendererObj, title(), x + 2, y, h);
  }

  private void drawTitle(FontRenderer fr, Supplier<String> title, int x1, int y, int h) {
    Icon<?> icon = icon();
    if (icon != null) {
      icon.drawIcon(++x1, y);
      x1 += 20;
    }
    fr.drawString(title.get(), x1, y + h / 2 - fr.FONT_HEIGHT / 2, 0xFFFFFF);
  }

  @Override public boolean clickRow(int mouseX, int mouseY) {
    return widget().click(mouseX, mouseY);
  }

  @Override public void releaseRow(int mouseX, int mouseY) {
    widget().release(mouseX, mouseY);
  }

  @Override public void keyTyped(char typedChar, int keyCode) {
    widget().keyTyped(typedChar, keyCode);
  }

  @Override public void setIsFocus(MutableBoolean isFocus) { }

  @Override public boolean folded() { return false; }

}
