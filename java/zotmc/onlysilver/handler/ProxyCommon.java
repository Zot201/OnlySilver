package zotmc.onlysilver.handler;

import static zotmc.onlysilver.Achievements.silverBowAch;
import static zotmc.onlysilver.Config.akkamaddiJoinWorld;
import static zotmc.onlysilver.Contents.silverBow;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class ProxyCommon {
	
	@SubscribeEvent public void onItemCrafted(ItemCraftedEvent event) {
		if (event.crafting.getItem() == silverBow.get())
			event.player.addStat(silverBowAch.get(), 1);
	}
	
	
	public int addArmor(String armor) {
		return 0;
	}

	public void registerHandlers() {
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(new JoinWorldHandler(akkamaddiJoinWorld.get()));
		
	}
	
	public void fmlEventBusClientRegister(Object handler) { }

}
