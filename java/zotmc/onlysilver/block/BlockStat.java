package zotmc.onlysilver.block;

import net.minecraft.block.Block;

import com.google.common.base.Supplier;

public final class BlockStat {
	
	public final int harvestLevel;
	public final float hardness, resistance;
	
	public BlockStat() {
		this(-1, 0, 0);
	}
	public BlockStat(int harvestLevel, float hardness, float resistance) {
		this.harvestLevel = harvestLevel;
		this.hardness = hardness;
		this.resistance = resistance;
	}
	
	public Block setStatTo(Supplier<Block> block, String toolClass) {
		Block ret = block.get()
				.setHardness(hardness)
				.setResistance(resistance);
		ret.setHarvestLevel(toolClass, harvestLevel);
		return ret;
	}

}
