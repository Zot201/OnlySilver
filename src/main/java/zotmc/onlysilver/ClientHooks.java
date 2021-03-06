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
package zotmc.onlysilver;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zotmc.onlysilver.loading.Patcher.*;
import zotmc.onlysilver.util.Utils;

@SuppressWarnings("WeakerAccess")
@SideOnly(Side.CLIENT)
public class ClientHooks {

  // silver aura rendering

  private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
  public static final ThreadLocal<EntityEquipmentSlot> renderArmorContext = new ThreadLocal<>();

  public static boolean renderSilverAura(RenderItem renderItem, IBakedModel model, ItemStack item) {
    if (Utils.hasEnch(item, Contents.silverAura)) {
      TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

      GlStateManager.depthMask(false);
      GlStateManager.depthFunc(514);
      GlStateManager.disableLighting();
      GlStateManager.blendFunc(768, 1);
      textureManager.bindTexture(RES_ITEM_GLINT);
      GlStateManager.matrixMode(5890);
      GlStateManager.pushMatrix();
      GlStateManager.scale(8.0F, 8.0F, 8.0F);
      float f = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
      GlStateManager.translate(f, 0.0F, 0.0F);
      GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
      ClientDelegates.renderModel(renderItem, model, 0xFFC0C0C0);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.scale(8.0F, 8.0F, 8.0F);
      float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
      GlStateManager.translate(-f1, 0.0F, 0.0F);
      GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
      ClientDelegates.renderModel(renderItem, model, 0xFFC0C0C0);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.blendFunc(770, 771);
      GlStateManager.enableLighting();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

      return true;
    }
    return false;
  }

  @Hook @Name("renderEnchantedGlint") @Srg("func_188364_a") @Return(condition = true) @Static(LayerArmorBase.class)
  public static boolean renderArmorSilverAura(RenderLivingBase<?> render, EntityLivingBase living, ModelBase model,
      float p3, float p4, float p5, float p6, float p7, float p8, float p9) {
    EntityEquipmentSlot slot = renderArmorContext.get();
    if (slot == null) return false;

    ItemStack item = living.getItemStackFromSlot(slot);

    if (item != null && Utils.hasEnch(item, Contents.silverAura)) {
      float f7 = living.ticksExisted + p5;
      render.bindTexture(RES_ITEM_GLINT);
      GlStateManager.enableBlend();
      GlStateManager.depthFunc(514);
      GlStateManager.depthMask(false);
      float f8 = 0.5F;
      GlStateManager.color(f8, f8, f8, 1.0F);

      for (int i = 0; i < 2; i++) {
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        float f9 = 0.76F;
        GlStateManager.color(0.75F * f9, 0.75F * f9, 0.75F * f9, 1.0F); // silver
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float f10 = 0.33333334F;
        GlStateManager.scale(f10, f10, f10);
        GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.0F, f7 * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
        GlStateManager.matrixMode(5888);
        model.render(living, p3, p4, p6, p7, p8, p9);
      }

      GlStateManager.matrixMode(5890);
      GlStateManager.loadIdentity();
      GlStateManager.matrixMode(5888);
      GlStateManager.enableLighting();
      GlStateManager.depthMask(true);
      GlStateManager.depthFunc(515);
      GlStateManager.disableBlend();

      return true;
    }

    return false;
  }

}
