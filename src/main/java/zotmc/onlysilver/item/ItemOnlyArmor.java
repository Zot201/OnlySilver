package zotmc.onlysilver.item;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import zotmc.onlysilver.CommonHooks;

public class ItemOnlyArmor extends ItemArmor {

	public ItemOnlyArmor(ArmorMaterial armorMaterial, int armorType) {
		super(armorMaterial, -1, armorType);
	}

	@Override public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return CommonHooks.isSilverIngot(repair);
	}

}
