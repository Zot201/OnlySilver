package zotmc.onlysilver.handler;

import static zotmc.onlysilver.Contents.silverBowAchievement;
import static zotmc.onlysilver.item.Instrumentum.silverBow;
import net.minecraftforge.common.MinecraftForge;
import zotmc.onlysilver.config.Config;
import zotmc.onlysilver.oregen.OnlySilverOreGen;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class ProxyCommon {
	
	@SubscribeEvent public void onItemCrafted(ItemCraftedEvent event) {
		if (event.crafting.getItem() == silverBow.get())
			event.player.addStat(silverBowAchievement.get(), 1);
	}
	
	
	public int addArmor(String armor) {
		return 0;
	}

	public void registerHandlers() {
		GameRegistry.registerWorldGenerator(OnlySilverOreGen.INSTANCE, 0);
		
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(new JoinWorldHandler(Config.current().enableAkka.get()));
		
	}
	
	public void fmlEventBusClientRegister(Object handler) { }

}
