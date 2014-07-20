package zotmc.onlysilver.handler;

import static net.minecraftforge.common.ChestGenHooks.DUNGEON_CHEST;
import static net.minecraftforge.common.ChestGenHooks.PYRAMID_DESERT_CHEST;
import static net.minecraftforge.common.ChestGenHooks.PYRAMID_JUNGLE_CHEST;
import static net.minecraftforge.common.ChestGenHooks.VILLAGE_BLACKSMITH;
import static net.minecraftforge.common.ChestGenHooks.getInfo;
import static zotmc.onlysilver.Contents.silverIngot;
import static zotmc.onlysilver.item.Instrumentum.silverBoots;
import static zotmc.onlysilver.item.Instrumentum.silverChest;
import static zotmc.onlysilver.item.Instrumentum.silverHelm;
import static zotmc.onlysilver.item.Instrumentum.silverLegs;
import net.minecraft.item.Item;
import net.minecraft.util.WeightedRandomChestContent;

public class LootManager {
	
	public static void addLoot() {
		getInfo(VILLAGE_BLACKSMITH).addItem(of(silverHelm.get(), 1, 1, 2));
		getInfo(VILLAGE_BLACKSMITH).addItem(of(silverChest.get(), 1, 1, 2));
		getInfo(VILLAGE_BLACKSMITH).addItem(of(silverLegs.get(), 1, 1, 2));
		getInfo(VILLAGE_BLACKSMITH).addItem(of(silverBoots.get(), 1, 1, 2));
		
		getInfo(PYRAMID_DESERT_CHEST).addItem(of(silverIngot.get(), 4, 6, 4));
		
		getInfo(PYRAMID_JUNGLE_CHEST).addItem(of(silverIngot.get(), 4, 6, 4));
		getInfo(PYRAMID_JUNGLE_CHEST).addItem(of(silverBoots.get(), 1, 1, 2));
		
		getInfo(DUNGEON_CHEST).addItem(of(silverIngot.get(), 3, 5, 1));
	}
	
	private static WeightedRandomChestContent of(Item item, int min, int max, int weight) {
		return new WeightedRandomChestContent(item, 0, min, max, weight);
	}
	
}
