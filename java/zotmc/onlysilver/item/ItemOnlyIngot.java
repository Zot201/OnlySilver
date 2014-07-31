package zotmc.onlysilver.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import zotmc.onlysilver.OnlySilver;
import zotmc.onlysilver.Recipes;
import zotmc.onlysilver.data.ModData.OnlySilvers;

public class ItemOnlyIngot extends Item {

	public ItemOnlyIngot(String name) {
		setTextureName(OnlySilvers.MODID + ":" + name);
		setUnlocalizedName(name);
		setCreativeTab(OnlySilver.instance.tabOnlySilver);
	}
	
	
	public static boolean isSilverIngot(ItemStack item) {
		for (ItemStack i : OreDictionary.getOres(Recipes.INGOT_SILVER))
			if (OreDictionary.itemMatches(i, item, false))
			return true;
		return false;
	}
	
}
