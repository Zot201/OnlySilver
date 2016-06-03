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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.apache.commons.lang3.mutable.MutableBoolean;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("Guava")
class CategoryRow implements Row {

  private final Icon<?> icon;
  private final Supplier<String> text;

  private CategoryRow(@Nullable Icon<?> icon, Supplier<String> text) {
    this.icon = icon;
    this.text = text;
  }

  public static CategoryRow create(Supplier<String> text) {
    return new CategoryRow(null, text);
  }

  static CategoryRow create(Icon<?> icon, Supplier<String> text) {
    return new CategoryRow(checkNotNull(icon), text);
  }

  @Override public void drawRow(int x, int y, int w, int h, int mouseX, int mouseY) {
    String s = text.get();
    FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
    int x1 = x + w / 2 - fr.getStringWidth(s) / 2;

    if (icon != null) {
      icon.drawIcon(x1 - 9, y);
      fr.drawString(s, x1 + 12, y - 2 + h - fr.FONT_HEIGHT, 0xFFFFFF);
    }
    else {
      fr.drawString(s, x1, y - 2 + h - fr.FONT_HEIGHT, 0xFFFFFF);
    }
  }

  @Override public boolean clickRow(int mouseX, int mouseY) { return false; }

  @Override public void releaseRow(int mouseX, int mouseY) { }

  @Override public void keyTyped(char typedChar, int keyCode) { }

  @Override public void setIsFocus(MutableBoolean isFocus) { }

  @Override public boolean folded() { return false; }

}
