package zotmc.onlysilver.config.gui;

import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.mutable.Mutable;

import zotmc.onlysilver.data.ModData.OnlySilvers;

public class TinyCheckbox implements Widget<TinyCheckbox> {

  private static final ResourceLocation textures = new ResourceLocation(OnlySilvers.MODID, "textures/gui/widgets.png");
  private final GuiTinyCheckbox delegate = new GuiTinyCheckbox();
  private final Mutable<Boolean> state;
  private final java.util.function.Consumer<Boolean> hoverHandler;

  public TinyCheckbox(Mutable<Boolean> state, java.util.function.Consumer<Boolean> hoverHandler) {
    this.state = state;
    this.hoverHandler = hoverHandler;
  }

  @Override public TinyCheckbox setLeftTop(int x, int y) {
    delegate.xPosition = x;
    delegate.yPosition = y;
    return this;
  }

  @Deprecated @Override public TinyCheckbox setWidthHeight(int w, int h) {
    return this;
  }

  @Override public void addTo(Collection<? super TinyCheckbox> collection) {
    collection.add(this);
  }

  @Override public void draw(int mouseX, int mouseY) {
    delegate.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
  }

  @Override public boolean click(int mouseX, int mouseY) {
    Minecraft mc = Minecraft.getMinecraft();
    if (delegate.mousePressed(mc, mouseX, mouseY)) {
      delegate.playPressSound(mc.getSoundHandler());
      state.setValue(!state.getValue());
      return true;
    }
    return false;
  }

  @Override public void release(int mouseX, int mouseY) { }

  @Override public void keyTyped(char typedChar, int keyCode) { }


  private class GuiTinyCheckbox extends GuiButton {
    public GuiTinyCheckbox() {
      super(-1, 0, 0, 6, 6, "");
    }

    @Override public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      if (visible) {
        ResourceLocation t = textures;
        mc.getTextureManager().bindTexture(t);
        GlStateManager.color(1, 1, 1, 1);
        hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);

        Boolean s = state.getValue();
        drawModalRectWithCustomSizedTexture(xPosition, yPosition, s ? 6 : 0, hovered ? 6 : 0, 6, 6, 16, 16);

        if (hovered) hoverHandler.accept(s);
      }
    }
  }

}
