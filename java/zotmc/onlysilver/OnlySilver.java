package zotmc.onlysilver;

import static net.minecraft.init.Items.enchanted_book;
import static zotmc.onlysilver.Contents.everlasting;
import static zotmc.onlysilver.Contents.incantation;
import static zotmc.onlysilver.Contents.silverIngot;
import static zotmc.onlysilver.OnlySilver.MODID;
import static zotmc.onlysilver.OnlySilver.NAME;

import java.util.EnumMap;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import zotmc.onlysilver.config.Config;
import zotmc.onlysilver.config.Config.ConfigState;
import zotmc.onlysilver.handler.ChannelHandler;
import zotmc.onlysilver.handler.LootManager;
import zotmc.onlysilver.handler.ProxyCommon;
import zotmc.onlysilver.handler.WerewolfHandler;
import zotmc.onlysilver.oregen.OnlySilverOreGen;
import zotmc.onlysilver.util.Holder;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
	
@Mod(modid = MODID, name = NAME, version = "1.9.3-1.7.2",
		dependencies = "required-after:Forge@[10.12.2.1121,);after:weaponmod;")
public class OnlySilver {
	
	public static final String
	MODID = "onlysilver",
	NAME = "OnlySilver",
	PACKAGE_NAME = "zotmc.onlysilver";
	
	@Instance(MODID) public static OnlySilver instance;
	
	@SidedProxy(
			clientSide = PACKAGE_NAME + ".handler.ProxyClient",
			serverSide = PACKAGE_NAME + ".handler.ProxyCommon")
	public static ProxyCommon proxy;
	
	public final Logger log = LogManager.getFormatterLogger(MODID);
	
	private EnumMap<Side, FMLEmbeddedChannel> channels;
	public static EnumMap<Side, FMLEmbeddedChannel> channels() {
		return instance.channels;
	}
	
	public final CreativeTabs tabOnlySilver = new CreativeTabs("tabOnlySilver") {
		@Override public Item getTabIconItem() {
			return silverIngot.get();
		}
		
		@SideOnly(Side.CLIENT) @SuppressWarnings("rawtypes")
		@Override public void displayAllReleventItems(List list) {
			super.displayAllReleventItems(list);
			
			if (everlasting.exists())
				enchanted_book.func_92113_a(everlasting.get(), list);
			if (incantation.exists())
				enchanted_book.func_92113_a(incantation.get(), list);
		}
	};
	
	
	
	@EventHandler public void preInit(FMLPreInitializationEvent event) {
		Config.init(
				new Configuration(event.getSuggestedConfigurationFile()),
				Holder.<ConfigState>absent()
		);
		
		Contents.init();
		Recipes.init();
		
	}
	
	@EventHandler public void init(FMLInitializationEvent event) {
		proxy.registerHandlers();
		
		channels = NetworkRegistry.INSTANCE.newChannel(MODID, new ChannelHandler());
		
		LootManager.addLoot();
		
		
		if (Config.current().enableWerewolf.get())
			try {
				MinecraftForge.EVENT_BUS.register(new WerewolfHandler());
			} catch (ClassNotFoundException ignored) {
			} catch (Throwable e) {
				log.catching(e);
			}
		
	}
	
	@EventHandler public void postInit(FMLPostInitializationEvent event) {
		OnlySilverOreGen.validateProfile();
		//System.out.println(Config.current().oreGenProfile.get());
	}
	
}
