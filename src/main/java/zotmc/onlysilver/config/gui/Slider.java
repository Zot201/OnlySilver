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

import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import zotmc.onlysilver.util.Utils;

import com.google.common.primitives.Doubles;

class Slider implements Widget<Slider> {

  interface Slidable {
    String getText();

    double getPosition();

    void setPosition(double position);

    void next();

    void previous();
  }

  private static final int W = 8;
  private final GuiSlider delegate = new GuiSlider();
  private final Slidable slidable;
  private int dragging = -1;
  private double tracking = -1;
  private int ticks;

  Slider(Slidable slidable) {
    this.slidable = slidable;
  }

  @Override public Slider setLeftTop(int x, int y) {
    delegate.xPosition = x;
    delegate.yPosition = y;
    return this;
  }

  @Override public Slider setWidthHeight(int w, int h) {
    delegate.width = w;
    delegate.height = h;
    return this;
  }

  @Override public void addTo(Collection<? super Slider> collection) {
    collection.add(this);
  }


  private int getHandle() {
    return delegate.xPosition + (int) Math.rint(slidable.getPosition() * (delegate.width - W));
  }

  @Override public void draw(int mouseX, int mouseY) {
    delegate.displayString = checkNotNull(slidable.getText());
    delegate.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
  }

  @Override public boolean click(int mouseX, int mouseY) {
    Minecraft mc = Minecraft.getMinecraft();

    if (delegate.mousePressed(mc, mouseX, mouseY)) {
      delegate.playPressSound(mc.getSoundHandler());

      int h = getHandle();

      if (mouseX < h) {
        slidable.previous();
        tracking = slidable.getPosition();
      }
      else if (mouseX >= h + W) {
        slidable.next();
        tracking = slidable.getPosition();
      }
      else {
        dragging = mouseX - h;
      }

      return true;
    }

    return false;
  }

  private void drag(int mouseX) {
    if (dragging >= 0) {
      double f = (mouseX - dragging - delegate.xPosition) / (double) (delegate.width - W);
      f = Math.max(0, Math.min(f, 1));
      if (Doubles.isFinite(f)) slidable.setPosition(f);
    }
    else if (tracking >= 0) {
      int k = mouseX - getHandle() - W / 2;
      double f = tracking + 1e-8 * Math.expm1(ticks++ / 8) * k / (delegate.width - W);
      double h = (mouseX - W / 2 - delegate.xPosition) / (double) (delegate.width - W);
      f = k < 0 ? Utils.max(h, 0, Math.min(f, 1)) : Math.max(0, Utils.min(f, 1, h));
      if (Doubles.isFinite(f)) slidable.setPosition(tracking = f);
    }
  }

  @Override public void release(int mouseX, int mouseY) {
    dragging = -1;
    tracking = -1;
    ticks = 0;
  }

  @Override public void keyTyped(char typedChar, int keyCode) { }


  private class GuiSlider extends Button.GuiQuarterButton {
    @Override protected int getHoverState(boolean mouseOver) {
      return 0;
    }

    @Override protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
      drag(mouseX);

      int x = getHandle(), y = yPosition, w = W / 2, h = height / 2;
      GlStateManager.color(1, 1, 1, 1);
      drawTexturedModalRect(x    , y    , 0          , 66                  ,     w,          h);
      drawTexturedModalRect(x + w, y    , 200 - W + w, 66                  , W - w,          h);
      drawTexturedModalRect(x    , y + h, 0          , 66 + 20 - height + h,     w, height - h);
      drawTexturedModalRect(x + w, y + h, 200 - W + w, 66 + 20 - height + h, W - w, height - h);
    }
  }

}
