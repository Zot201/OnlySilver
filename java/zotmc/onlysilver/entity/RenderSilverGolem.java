package zotmc.onlysilver.entity;

import static zotmc.onlysilver.OnlySilver.MODID;
import net.minecraft.client.renderer.entity.RenderIronGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;

public class RenderSilverGolem extends RenderIronGolem {
	
    private static final ResourceLocation silverGolemTextures =
    		new ResourceLocation(MODID + ":textures/entity/silver_golem.png");
    
    @Override protected ResourceLocation getEntityTexture(EntityIronGolem ironGolem) {
    	return silverGolemTextures;
    }

}
