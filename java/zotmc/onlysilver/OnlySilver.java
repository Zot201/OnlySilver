package zotmc.onlysilver;

import static net.minecraft.item.Item.itemRegistry;
import static zotmc.onlysilver.Config.blockDefinitions;
import static zotmc.onlysilver.Config.oreGenerationProfile;
import static zotmc.onlysilver.Contents.silverAxe;
import static zotmc.onlysilver.OnlySilver.DEPENDENCIES;
import static zotmc.onlysilver.OnlySilver.MODID;
import static zotmc.onlysilver.OnlySilver.NAME;

import java.util.EnumMap;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import zotmc.onlysilver.handler.ChannelHandler;
import zotmc.onlysilver.handler.LootManager;
import zotmc.onlysilver.handler.OreGenerator;
import zotmc.onlysilver.handler.ProxyCommon;
import zotmc.onlysilver.handler.WerewolfHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
	
@Mod(modid = MODID, name = NAME, version = "1.7.6-1.7.2", dependencies = DEPENDENCIES)
public class OnlySilver {
	
	public static final String
	MODID = "onlysilver",
	NAME = "OnlySilver",
	DEPENDENCIES = "after:weaponmod;",
	
	PACKAGE_NAME = "zotmc.onlysilver";
	
	@Instance(MODID) public static OnlySilver instance;
	
	@SidedProxy(
			clientSide = PACKAGE_NAME + ".handler.ProxyClient",
			serverSide = PACKAGE_NAME + ".handler.ProxyCommon")
	public static ProxyCommon proxy;
	
	public static EnumMap<Side, FMLEmbeddedChannel> channels;
	
	public static final CreativeTabs
	TAB_ONLY_SILVER = new TabOnlySilver();
	
	
	@EventHandler public void preInit(FMLPreInitializationEvent event) {
		Config.init(event);
		
		Contents.init();
		Recipes.init();
		Achievements.init();
		
	}
	
	@EventHandler public void init(FMLInitializationEvent event) {
		proxy.registerHandlers();
		
		channels = NetworkRegistry.INSTANCE.newChannel(MODID, new ChannelHandler());
		
		LootManager.addLoot();
		
		
		
		if (Config.werewolfEffectiveness.get())
			try {
				MinecraftForge.EVENT_BUS.register(new WerewolfHandler());
			} catch (Exception ignored) { }
		
		
		if (Loader.isModLoaded("TreeCapitator")) {
			NBTTagCompound c = new NBTTagCompound();
			c.setString("modID", MODID);
			c.setString("axeIDList", itemRegistry.getNameForObject(silverAxe.get()));
			FMLInterModComms.sendMessage("TreeCapitator", "ThirdPartyModConfig", c);
		}
		
	}
	
	@EventHandler public void postInit(FMLPostInitializationEvent event) {
		OreGenerator oreGenerator = new OreGenerator(oreGenerationProfile.get(), blockDefinitions.get());
		//System.out.println(oreGenerator);
		GameRegistry.registerWorldGenerator(oreGenerator, 0);
		
	}
	
	
}