package zotmc.onlysilver.item;

import zotmc.onlysilver.OnlySilver;
import zotmc.onlysilver.Recipes;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemOnlyArmor extends ItemArmor {
	private String texture;

	public ItemOnlyArmor(ArmorMaterial enumarmormaterial, int j, int k, String loc) {
		super(enumarmormaterial, j, k);
		this.texture = loc;
		setCreativeTab(OnlySilver.TAB_ONLY_SILVER);
	}

	@Override public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon("onlysilver:" + this.texture);
	}
	
	@Override public String getArmorTexture(ItemStack itemstack, Entity entity, int slot, String type) {
		if (slot > 3 || slot < 0)
			return null;
		return "onlysilver:textures/models/armor/silver_" + (slot == 2 ? 2 : 1) +".png";
	}

	@Override public boolean getIsRepairable(ItemStack toolToRepair, ItemStack material) {
		for (ItemStack i : OreDictionary.getOres(Recipes.SILVER_INGOT))
			if (OreDictionary.itemMatches(i, material, false))
				return true;
		return super.getIsRepairable(toolToRepair, material);
	}
	
}
