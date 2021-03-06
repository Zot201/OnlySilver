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
package zotmc.onlysilver.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zotmc.onlysilver.ClientDelegates;

@SideOnly(Side.CLIENT)
public class ClientUtils {

  public static void color(int color) {
    int r = color >> 16 & 0xFF, g = color >> 8 & 0xFF, b = color & 0xFF, a = color >> 24 & 0xFF;
    GlStateManager.color(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
  }

  /**
   * @see RenderItem#renderItemModelIntoGUI
   */
  public static void renderItemIntoGUI(ItemStack stack, int x, int y, int color, boolean renderEffect) {
    Minecraft mc = Minecraft.getMinecraft();
    RenderItem ri = mc.getRenderItem();
    TextureManager tm = mc.getTextureManager();

    IBakedModel model = ri.getItemModelMesher().getItemModel(stack);
    GlStateManager.pushMatrix();
    tm.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    tm.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
    GlStateManager.enableRescaleNormal();
    GlStateManager.enableAlpha();
    GlStateManager.alphaFunc(516, 0.1f);
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
    color(color);
    ClientDelegates.setupGuiTransform(ri, x, y, model.isGui3d());
    model = ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.GUI, false);
    renderItem(ri, stack, model, color, renderEffect);
    GlStateManager.disableAlpha();
    GlStateManager.disableRescaleNormal();
    GlStateManager.disableLighting();
    GlStateManager.popMatrix();
    tm.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    tm.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
  }

  private static void renderItem(RenderItem ri, ItemStack stack, IBakedModel model,
      int color, boolean renderEffect) {

    GlStateManager.pushMatrix();
    GlStateManager.translate(-0.5f, -0.5f, -0.5f);

    if (model.isBuiltInRenderer()) {
      color(color);
      GlStateManager.enableRescaleNormal();
      TileEntityItemStackRenderer.instance.renderByItem(stack);
    }
    else {
      ClientDelegates.renderModel(ri, model, color, stack);

      if (renderEffect && stack.hasEffect()) {
        ClientDelegates.renderEffect(ri, model);
      }
    }

    GlStateManager.popMatrix();
  }

}
