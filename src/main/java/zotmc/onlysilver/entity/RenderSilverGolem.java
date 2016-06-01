package zotmc.onlysilver.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderIronGolem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zotmc.onlysilver.data.ModData.OnlySilvers;

@SideOnly(Side.CLIENT)
public class RenderSilverGolem extends RenderIronGolem {
	
	private static final ResourceLocation silverGolemTextures =
			new ResourceLocation(OnlySilvers.MODID + ":textures/entity/silver_golem.png");
	
	public RenderSilverGolem(RenderManager renderManager) {
		super(renderManager);
	}
	
	@Override protected ResourceLocation getEntityTexture(EntityIronGolem ironGolem) {
		return silverGolemTextures;
	}
	
	@Override protected void preRenderCallback(EntityLivingBase living, float f) {
		GlStateManager.scale(8/14f, 19/29f, 8/14f);
	}
	
}
