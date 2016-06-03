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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import com.google.common.base.Supplier;

import javax.annotation.Nullable;

@SuppressWarnings("Guava")
class Title implements Element {

  private final Icon<?> icon;
  private final Supplier<String> text;
  private final int x, y;

  Title(Supplier<String> text, int x, int y) {
    this(null, text, x, y);
  }

  Title(@Nullable Icon<?> icon, Supplier<String> text, int x, int y) {
    this.icon = icon;
    this.text = checkNotNull(text);
    this.x = x;
    this.y = y;
  }

  @Override public void draw(int mouseX, int mouseY) {
    String s = text.get();
    FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
    int x1 = x - fr.getStringWidth(s) / 2;

    if (icon != null) {
      icon.drawIcon(x1 - 9, y - 5);
      fr.drawStringWithShadow(s, x1 + 12, y + 9 - fr.FONT_HEIGHT, 0xFFFFFF);
    }
    else {
      fr.drawStringWithShadow(s, x1, y + 9 - fr.FONT_HEIGHT, 0xFFFFFF);
    }
  }

  @Override public boolean click(int mouseX, int mouseY) {
    return false;
  }

  @Override public void release(int mouseX, int mouseY) { }

  @Override public void keyTyped(char typedChar, int keyCode) { }

}
