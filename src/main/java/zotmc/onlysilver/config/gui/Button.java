package zotmc.onlysilver.config.gui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import com.google.common.base.Supplier;

public class Button implements Widget<Button> {

	private final GuiButton delegate = new GuiQuarterButton();
	private final Runnable action;
	private final Supplier<String> text;

	public Button(Runnable action, Supplier<String> text) {
		this.action = action;
		this.text = text;
	}

	@Override public Button setLeftTop(int x, int y) {
		delegate.xPosition = x;
		delegate.yPosition = y;
		return this;
	}

	@Override public Button setWidthHeight(int w, int h) {
		delegate.width = w;
		delegate.height = h;
		return this;
	}

	@Override public void addTo(Collection<? super Button> collection) {
		collection.add(this);
	}


	@Override public void draw(int mouseX, int mouseY) {
		delegate.displayString = checkNotNull(text.get());
		delegate.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
	}

	@Override public boolean click(int mouseX, int mouseY) {
		Minecraft mc = Minecraft.getMinecraft();
		if (delegate.mousePressed(mc, mouseX, mouseY)) {
			delegate.playPressSound(mc.getSoundHandler());
			action.run();
			return true;
		}
		return false;
	}

	@Override public void release(int mouseX, int mouseY) { }

	@Override public void keyTyped(char typedChar, int keyCode) { }


	static class GuiQuarterButton extends GuiButton {
		public GuiQuarterButton() {
			super(-1, 0, 0, 12, 12, "");
		}

		@Override public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (visible) {
				mc.getTextureManager().bindTexture(buttonTextures);
				GlStateManager.color(1, 1, 1, 1);
				hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
				int k = getHoverState(hovered);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
				GlStateManager.blendFunc(770, 771);

				int x = xPosition, y = yPosition, w = width / 2, h = height / 2, k1 = 46 + k * 20;
				drawTexturedModalRect(x    , y    , 0                  , k1                  ,         w,          h);
				drawTexturedModalRect(x + w, y    , 0 + 200 - width + w, k1                  , width - w,          h);
				drawTexturedModalRect(x    , y + h, 0                  , k1 + 20 - height + h,         w, height - h);
				drawTexturedModalRect(x + w, y + h, 0 + 200 - width + w, k1 + 20 - height + h, width - w, height - h);

				mouseDragged(mc, mouseX, mouseY);
				
				if (displayString.length() > 0) {
					int l = 14737632;
					if (packedFGColour != 0) l = packedFGColour;
					else if (!enabled) l = 10526880;
					else if (hovered) l = 16777120;

					drawCenteredString(mc.fontRendererObj, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, l);
				}
			}
		}
	}

}
