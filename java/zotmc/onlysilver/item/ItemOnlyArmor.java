package zotmc.onlysilver.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemOnlyArmor extends ItemArmor {

	public ItemOnlyArmor(ArmorMaterial enumarmormaterial, int j, int k) {
		super(enumarmormaterial, j, k);
	}
	
	@Override public String getArmorTexture(ItemStack itemstack, Entity entity, int slot, String type) {
		if (slot > 3 || slot < 0)
			return null;
		return "onlysilver:textures/models/armor/silver_" + (slot == 2 ? 2 : 1) +".png";
	}
	
	@Override public boolean getIsRepairable(ItemStack toRepair, ItemStack toRepairWith) {
		return ItemOnlyIngot.isSilverIngot(toRepairWith);
	}
	
}
