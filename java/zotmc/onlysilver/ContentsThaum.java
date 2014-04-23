package zotmc.onlysilver;

import static thaumcraft.api.aspects.Aspect.EARTH;
import static thaumcraft.api.aspects.Aspect.GREED;
import static thaumcraft.api.aspects.Aspect.METAL;
import static zotmc.onlysilver.Contents.silverBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import zotmc.onlysilver.entity.EntitySilverGolem;

public class ContentsThaum {
	
	static void init() {
		ThaumcraftApi.registerEntityTag(
				getEntityString(EntitySilverGolem.class),
				new AspectList()
					.add(METAL, 4)
					.add(GREED, 3)
					.add(EARTH, 3));
		
		ThaumcraftApi.registerObjectTag(
				new ItemStack(silverBlock.get()),
				new AspectList()
					.add(METAL, 8)
					.add(GREED, 8));
		
	}
	
	private static String getEntityString(Class<? extends Entity> clz) {
		return (String) EntityList.classToStringMapping.get(clz);
	}

}
