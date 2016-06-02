package zotmc.onlysilver.config;

import java.util.Objects;

import net.minecraft.block.Block;

public final class BlockStats {

  public final int harvestLevel;
  public final float hardness, resistance;

  public BlockStats(int harvestLevel, float hardness, float resistance) {
    this.harvestLevel = harvestLevel;
    this.hardness = hardness;
    this.resistance = resistance;
  }

  public void setStatTo(Block block, String toolClass) {
    block.setHardness(hardness);
    block.setResistance(resistance);
    if (toolClass != null) block.setHarvestLevel(toolClass, harvestLevel);
  }

  @Override public int hashCode() {
    return Objects.hash(harvestLevel, hardness, resistance);
  }

  @Override public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj instanceof BlockStats) {
      BlockStats o = (BlockStats) obj;
      return harvestLevel == o.harvestLevel && hardness == o.hardness && resistance == o.resistance;
    }
    return false;
  }

}
