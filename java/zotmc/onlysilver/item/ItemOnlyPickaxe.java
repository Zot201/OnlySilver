package zotmc.onlysilver.item;

import zotmc.onlysilver.OnlySilver;
import zotmc.onlysilver.Recipes;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemOnlyPickaxe extends ItemPickaxe {
	private String texture;

	public ItemOnlyPickaxe(ToolMaterial enumtoolmaterial, String loc) {
		super(enumtoolmaterial);
		this.texture = loc;
		setCreativeTab(OnlySilver.TAB_ONLY_SILVER);
	}

	@Override public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon("onlysilver:" + this.texture);
	}

	@Override public boolean getIsRepairable(ItemStack toolToRepair, ItemStack material) {
		for (ItemStack i : OreDictionary.getOres(Recipes.SILVER_INGOT))
			if (OreDictionary.itemMatches(i, material, false))
				return true;
		return super.getIsRepairable(toolToRepair, material);
	}
	
}
