package zotmc.onlysilver.item;

import zotmc.onlysilver.OnlySilver;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

public class ItemOnlyIngot extends Item {
	private String texture;

	public ItemOnlyIngot(String loc) {
		this.texture = loc;
		setCreativeTab(OnlySilver.TAB_ONLY_SILVER);
	}

	@Override public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon("onlysilver:" + this.texture);
	}
}
