package zotmc.onlysilver;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zotmc.onlysilver.loading.Patcher.Hook;
import zotmc.onlysilver.loading.Patcher.Name;
import zotmc.onlysilver.loading.Patcher.Return;
import zotmc.onlysilver.loading.Patcher.Static;
import zotmc.onlysilver.util.Utils;

@SideOnly(Side.CLIENT)
public class ClientHooks {

  // silver aura rendering

  private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
  public static final ThreadLocal<Integer> renderArmorContext = new ThreadLocal<>();

  public static boolean renderSilverAura(RenderItem renderItem,
      @SuppressWarnings("deprecation") net.minecraft.client.resources.model.IBakedModel model, ItemStack item) {

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
      textureManager.bindTexture(TextureMap.locationBlocksTexture);

      return true;
    }
    return false;
  }

  @Hook @Name("func_177183_a") @Return(condition = true) @Static(LayerArmorBase.class)
  public static boolean renderArmorSilverAura(EntityLivingBase living, ModelBase model,
      float p3, float p4, float p5, float p6, float p7, float p8, float p9) {

    Integer slot = renderArmorContext.get();
    if (slot == null) return false;

    ItemStack item = living.getCurrentArmor(slot - 1);

    if (Utils.hasEnch(item, Contents.silverAura)) {
      TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

      float f7 = living.ticksExisted + p5;
      textureManager.bindTexture(RES_ITEM_GLINT);
      GlStateManager.enableBlend();
      GlStateManager.depthFunc(514);
      GlStateManager.depthMask(false);
      float f8 = 0.5F;
      GlStateManager.color(f8, f8, f8, 1.0F);

      for (int i = 0; i < 2; ++i) {
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(768, 1);
        float f9 = 0.76F;
        GlStateManager.color(0.75F * f9, 0.75F * f9, 0.75F * f9, 1.0F);
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
