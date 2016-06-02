package zotmc.onlysilver.config;

import java.util.Objects;

import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;

public final class ToolStats {

  public final int harvestLevel, maxUses;
  public final float efficiency, damage;
  public final int enchantability;

  public ToolStats(int harvestLevel, int maxUses, float efficiency, float damage, int enchantability) {
    this.harvestLevel = harvestLevel;
    this.maxUses = maxUses;
    this.efficiency = efficiency;
    this.damage = damage;
    this.enchantability = enchantability;
  }

  public ToolMaterial addToolMaterial(String name) {
    return EnumHelper.addToolMaterial(name, harvestLevel, maxUses, efficiency, damage, enchantability);
  }

  @Override public int hashCode() {
    return Objects.hash(harvestLevel, maxUses, efficiency, damage, enchantability);
  }

  @Override public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj instanceof ToolStats) {
      ToolStats o = (ToolStats) obj;
      return harvestLevel == o.harvestLevel && maxUses == o.maxUses && efficiency == o.efficiency
          && damage == o.damage && enchantability == o.enchantability;
    }
    return false;
  }

}
