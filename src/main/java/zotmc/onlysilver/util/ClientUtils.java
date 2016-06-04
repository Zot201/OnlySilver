package zotmc.onlysilver.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
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
    GlStateManager.blendFunc(770, 771);
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
    GlStateManager.scale(0.5f, 0.5f, 0.5f);

    boolean isBuiltInRenderer = model.isBuiltInRenderer();
    if (isBuiltInRenderer) {
      GlStateManager.rotate(180, 0, 1, 0);
      GlStateManager.translate(-0.5f, -0.5f, -0.5f);
      color(color);
      GlStateManager.enableRescaleNormal();
      TileEntityItemStackRenderer.instance.renderByItem(stack);
    }
    else {
      GlStateManager.translate(-0.5f, -0.5f, -0.5f);
      ClientDelegates.renderModel(ri, model, color, stack);

      if (renderEffect && stack.hasEffect()) ClientDelegates.renderEffect(ri, model);
    }

    GlStateManager.popMatrix();
  }

}
