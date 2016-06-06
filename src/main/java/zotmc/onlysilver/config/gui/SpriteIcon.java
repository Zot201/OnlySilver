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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
    VertexBuffer buffer = tessellator.getBuffer();
    buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
    buffer.pos(x    , y + h, z).tex((u    ) / r, (v + h) / r).endVertex();
    buffer.pos(x + w, y + h, z).tex((u + w) / r, (v + h) / r).endVertex();
    buffer.pos(x + w, y    , z).tex((u + w) / r, (v    ) / r).endVertex();
    buffer.pos(x    , y    , z).tex((u    ) / r, (v    ) / r).endVertex();
    tessellator.draw();
  }

}
