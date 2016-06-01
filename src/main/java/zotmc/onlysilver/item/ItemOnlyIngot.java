package zotmc.onlysilver.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemOnlyIngot extends Item {
	
	@Override public boolean isBeaconPayment(ItemStack stack) {
		return true;
	}

}
