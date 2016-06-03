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

import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

class TextField implements Widget<TextField> {

  interface Handler {
    boolean getIsFocus();

    String getText();

    void setText(String text);
  }

  private final GuiTextField delegate = new GuiTextField(-1, Minecraft.getMinecraft().fontRendererObj, 0, 0, 0, 0);
  private final Handler handler;
  private boolean isFocus;
  private String text;

  TextField(Handler handler) {
    this.handler = handler;
    delegate.setMaxStringLength(Short.MAX_VALUE);
    delegate.setFocused(isFocus = handler.getIsFocus());
    delegate.setText(text = handler.getText());
    delegate.setCursorPositionZero();
  }

  @Override public TextField setLeftTop(int x, int y) {
    delegate.xPosition = x + 2;
    delegate.yPosition = y + 2;
    return this;
  }

  @Override public TextField setWidthHeight(int w, int h) {
    delegate.width = w - 4;
    delegate.height = h - 4;
    return this;
  }

  @Override public void addTo(Collection<? super TextField> collection) {
    collection.add(this);
  }


  @Override public void draw(int mouseX, int mouseY) {
    boolean isFocus = handler.getIsFocus();
    if (this.isFocus ^ isFocus) delegate.setFocused(this.isFocus = isFocus);

    String text = handler.getText();
    //noinspection StringEquality
    if (this.text != text) {
      delegate.setText(this.text = text);
      delegate.setCursorPositionZero();
    }

    delegate.drawTextBox();
    if (isFocus) delegate.updateCursorCounter();
  }

  @Override public boolean click(int mouseX, int mouseY) {
    delegate.mouseClicked(mouseX, mouseY, 0);
    return isFocus = delegate.isFocused();
  }

  @Override public void release(int mouseX, int mouseY) { }

  @Override public void keyTyped(char typedChar, int keyCode) {
    delegate.textboxKeyTyped(typedChar, keyCode);

    String text = delegate.getText();
    //noinspection StringEquality
    if (this.text != text) handler.setText(this.text = text);
  }

}
