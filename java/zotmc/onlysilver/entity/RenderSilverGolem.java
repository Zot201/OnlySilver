package zotmc.onlysilver.entity;

import net.minecraft.client.renderer.entity.RenderIronGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;
import zotmc.onlysilver.data.ModData.OnlySilvers;

public class RenderSilverGolem extends RenderIronGolem {
	
    private static final ResourceLocation silverGolemTextures =
    		new ResourceLocation(OnlySilvers.MODID + ":textures/entity/silver_golem.png");
    
    @Override protected ResourceLocation getEntityTexture(EntityIronGolem ironGolem) {
    	return silverGolemTextures;
    }

}
