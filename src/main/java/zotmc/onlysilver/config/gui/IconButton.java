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

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import zotmc.onlysilver.data.LangData;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class IconButton implements Widget<IconButton> {

  interface Handler {
    Icon<?> icon();

    boolean isCommon();

    boolean getState();

    void toggleState();

    @Nullable Holder<List<String>> hoveringTextHolder();

    @Nullable List<String> getHoveringTexts();
  }

  private static final ResourceLocation achievementBackground =
      new ResourceLocation("textures/gui/achievement/achievement_background.png");
  private final GuiIconButton delegate = new GuiIconButton();
  private final Handler handler;

  IconButton(Handler handler) {
    this.handler = handler;
  }

  @Override public IconButton setLeftTop(int x, int y) {
    delegate.xPosition = x;
    delegate.yPosition = y;
    return this;
  }

  @Deprecated @Override public IconButton setWidthHeight(int w, int h) {
    return this;
  }

  @Override public void addTo(Collection<? super IconButton> collection) {
    collection.add(this);
  }


  @Override public void draw(int mouseX, int mouseY) {
    delegate.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
  }

  @Override public boolean click(int mouseX, int mouseY) {
    Minecraft mc = Minecraft.getMinecraft();
    if (delegate.mousePressed(mc, mouseX, mouseY)) {
      delegate.playPressSound(mc.getSoundHandler());
      handler.toggleState();
      return true;
    }
    return false;
  }

  @Override public void release(int mouseX, int mouseY) {
  }

  @Override public void keyTyped(char typedChar, int keyCode) {
  }


  private class GuiIconButton extends GuiButton {
    GuiIconButton() {
      super(-1, 0, 0, 22, 22, "");
    }

    @Override public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      if (visible) {
        int x = xPosition, y = yPosition;

        hovered =
            mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
        float brightness = hovered ? 1 : 0.75F;
        boolean state = handler.getState();
        if (!state) {
          brightness /= 2.5F;
        }
        GlStateManager.color(brightness, brightness, brightness, 1);

        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        mc.getTextureManager().bindTexture(achievementBackground);
        mc.getRenderItem().isNotRenderingEffectsInGUI(false);
        drawTexturedModalRect(x - 2, y - 2, handler.isCommon() ? 0 : 26, 202, 26, 26);

        handler.icon()
            .setBrightness(brightness)
            .drawIcon(x + 3, y + 3);

        if (hovered) {
          Holder<List<String>> holder = handler.hoveringTextHolder();

          if (holder != null) {
            List<String> texts = handler.getHoveringTexts();

            if (texts != null) {
              if (texts.size() > 0) {
                texts = Lists.newLinkedList(texts);
                texts.add(1, state ? TextFormatting.GREEN + LangData.ENABLED.get()
                    : TextFormatting.DARK_RED + LangData.DISABLED.get());
              }

              holder.set(texts);
            }
          }
        }
      }
    }
  }

}
