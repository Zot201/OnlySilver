package zotmc.onlysilver.item;

import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;

import com.google.common.base.Supplier;

public final class ToolStat {
	
	public final int harvestLevel, maxUses, enchantability;
	public final float efficiency, damage;
	
	public ToolStat() {
		this(0, 0, 0, 0, 0);
	}
	public ToolStat(int harvestLevel, int maxUses, float efficiency, float damage, int enchantability) {
		this.harvestLevel = harvestLevel;
		this.maxUses = maxUses;
		this.efficiency = efficiency;
		this.damage = damage;
		this.enchantability = enchantability;
	}
	public ToolStat(int harvestLevel, int maxUses, double efficiency, double damage, int enchantability) {
		this(harvestLevel, maxUses, (float) efficiency, (float) damage, enchantability);
	}
	
	public ToolMaterial addToolMaterial(String name) {
		return EnumHelper.addToolMaterial(name, harvestLevel, maxUses, efficiency, damage, enchantability);
	}

}
