package zotmc.onlysilver.config.gui;

import java.util.Collection;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import zotmc.onlysilver.data.LangData;

import com.google.common.collect.Lists;

// TODO: refinement
public class IconButton implements Widget<IconButton> {
	
	public interface Handler {
		public Icon<?> icon();
		
		public boolean isCommon();
		
		public boolean getState();
		
		public void toggleState();
		
		public Holder<List<String>> hoveringTextHolder();
		
		public List<String> getHoveringTexts();
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

	@Override public void release(int mouseX, int mouseY) { }

	@Override public void keyTyped(char typedChar, int keyCode) { }


	private class GuiIconButton extends GuiButton {
		public GuiIconButton() {
			super(-1, 0, 0, 22, 22, "");
		}

		@Override public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (visible) {
				int x = xPosition, y = yPosition;

				hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
				float brightness = hovered ? 1 : 0.75F;
				boolean state = handler.getState();
				if (!state) brightness /= 2.5F;
				GlStateManager.color(brightness, brightness, brightness, 1);
				
				GlStateManager.enableBlend();
				GlStateManager.depthMask(false);
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				mc.getTextureManager().bindTexture(achievementBackground);
				mc.getRenderItem().func_175039_a(false);
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
								texts.add(1, state ? EnumChatFormatting.GREEN + LangData.ENABLED.get()
										: EnumChatFormatting.DARK_RED + LangData.DISABLED.get());
							}
							
							holder.set(texts);
						}
					}
				}
			}
		}
	}

}
