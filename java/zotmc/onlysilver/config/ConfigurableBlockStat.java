package zotmc.onlysilver.config;

import net.minecraftforge.common.config.Configuration;
import zotmc.onlysilver.block.BlockStat;

public class ConfigurableBlockStat extends Configurable<BlockStat> {
	
	private BlockStat value = new BlockStat();

	public ConfigurableBlockStat(String category, String blockName) {
		super(category, blockName);
	}

	@Override public BlockStat get() {
		return value;
	}

	@Override Configurable<BlockStat> set(BlockStat value) {
		this.value = value;
		return this;
	}
	
	protected String harvestLevel() {
		return key + " Harvest Level";
	}
	protected String hardness() {
		return key + " Hardness";
	}
	protected String resistance() {
		return key + " Resistance";
	}

	@Override void load(Configuration configFile) {
		value = new BlockStat(
				configFile.get(category, harvestLevel(), value.harvestLevel).getInt(value.harvestLevel),
				(float) configFile.get(category, hardness(), value.hardness).getDouble(value.hardness),
				(float) configFile.get(category, resistance(), value.resistance).getDouble(value.resistance)
		);
	}

	@Override void save(Configuration configFile) {
		configFile.get(category, harvestLevel(), 0).set(value.harvestLevel);
		configFile.get(category, hardness(), 0).set(value.hardness);
		configFile.get(category, resistance(), 0).set(value.resistance);
		
		configFile.save();
	}
	
}
