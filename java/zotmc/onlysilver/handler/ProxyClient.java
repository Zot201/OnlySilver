package zotmc.onlysilver.handler;

import static zotmc.onlysilver.item.Instrumentum.silverBow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import zotmc.onlysilver.entity.EntitySilverGolem;
import zotmc.onlysilver.entity.RenderSilverGolem;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ProxyClient extends ProxyCommon {
	
	@SubscribeEvent public void onFOVUpdate(FOVUpdateEvent event) {
		EntityPlayer player = event.entity;
		if (player.isUsingItem() && player.getItemInUse().getItem() == silverBow.get())
			event.newfov = getFOVMultiplier(player);
	}
	private float getFOVMultiplier(EntityPlayer player) {
		int i = player.getItemInUseDuration();
		float f1 = i / 20.0F;
		if (f1 > 1.0F)
			f1 = 1.0F;
		else
			f1 *= f1;
		return (1.0F - f1 * 0.15F);
	}
	
	
	
	@Override public int addArmor(String armor) {
		return RenderingRegistry.addNewArmourRendererPrefix(armor);
	}
	
	@Override public void registerHandlers() {
		super.registerHandlers();
		
		MinecraftForge.EVENT_BUS.register(this);
		RenderingRegistry.registerEntityRenderingHandler(EntitySilverGolem.class, new RenderSilverGolem());
	}
	
	@Override public void fmlEventBusClientRegister(Object handler) {
		FMLCommonHandler.instance().bus().register(handler);
	}
	
}
