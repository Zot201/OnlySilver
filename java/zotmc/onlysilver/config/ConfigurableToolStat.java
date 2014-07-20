package zotmc.onlysilver.config;

import net.minecraftforge.common.config.Configuration;
import zotmc.onlysilver.item.ToolStat;

public class ConfigurableToolStat extends Configurable<ToolStat> {
	
	private ToolStat value = new ToolStat();

	public ConfigurableToolStat(String category, String materialName) {
		super(category, materialName);
	}

	@Override public ToolStat get() {
		return value;
	}

	@Override Configurable<ToolStat> set(ToolStat value) {
		this.value = value;
		return this;
	}
	
	protected String harvestLevel() {
		return key + " Tool Harvest Level";
	}
	protected String maxUses() {
		return key + " Tool Max Uses";
	}
	protected String efficiency() {
		return key + " Tool Efficiency";
	}
	protected String damage() {
		return key + " Tool Damage";
	}
	protected String enchantability() {
		return key + " Tool Enchantability";
	}

	@Override void load(Configuration configFile) {
		value = new ToolStat(
				configFile.get(category, harvestLevel(), value.harvestLevel).getInt(value.harvestLevel),
				configFile.get(category, maxUses(), value.maxUses).getInt(value.maxUses),
				configFile.get(category, efficiency(), value.efficiency).getDouble(value.efficiency),
				configFile.get(category, damage(), value.damage).getDouble(value.damage),
				configFile.get(category, enchantability(), value.enchantability).getInt(value.enchantability)
		);
	}

	@Override void save(Configuration configFile) {
		configFile.get(category, harvestLevel(), 0).set(value.harvestLevel);
		configFile.get(category, maxUses(), 0).set(value.maxUses);
		configFile.get(category, efficiency(), 0).set(value.efficiency);
		configFile.get(category, damage(), 0).set(value.damage);
		configFile.get(category, enchantability(), 0).set(value.enchantability);
		
		configFile.save();
	}

}
