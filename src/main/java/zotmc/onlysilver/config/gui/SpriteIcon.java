package zotmc.onlysilver.config.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.ResourceLocation;
import zotmc.onlysilver.util.ClientUtils;

public class SpriteIcon extends Icon<SpriteIcon> {

	private final ResourceLocation res;
	private final int u, v, w, h;
	private final float r;

	public SpriteIcon(ResourceLocation res, int u, int v, float r) {
		this(res, u, v, 18, 18, r);
	}
	public SpriteIcon(ResourceLocation res, int u, int v, int w, int h, float r) {
		this.res = res;
		this.u = u;
		this.v = v;
		this.w = w;
		this.h = h;
		this.r = r;
	}

	@Override public void drawIcon(int x, int y, int z) {
		x -= (w - 16) / 2;
		y -= (h - 16) / 2;

		GlStateManager.enableAlpha();
		ClientUtils.color(color);
		Minecraft.getMinecraft().getTextureManager().bindTexture(res);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.startDrawingQuads();
		worldrenderer.addVertexWithUV(x + 0, y + h, z, (u + 0) / r, (v + h) / r);
		worldrenderer.addVertexWithUV(x + w, y + h, z, (u + w) / r, (v + h) / r);
		worldrenderer.addVertexWithUV(x + w, y + 0, z, (u + w) / r, (v + 0) / r);
		worldrenderer.addVertexWithUV(x + 0, y + 0, z, (u + 0) / r, (v + 0) / r);
		tessellator.draw();
	}

}