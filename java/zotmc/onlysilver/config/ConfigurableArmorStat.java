package zotmc.onlysilver.config;

import net.minecraftforge.common.config.Configuration;
import zotmc.onlysilver.item.ArmorStat;

public class ConfigurableArmorStat extends Configurable<ArmorStat> {
	
	private ArmorStat value = new ArmorStat();

	public ConfigurableArmorStat(String category, String materialName) {
		super(category, materialName);
	}

	@Override public ArmorStat get() {
		return value;
	}

	@Override Configurable<ArmorStat> set(ArmorStat value) {
		this.value = value;
		return this;
	}
	
	protected String durability() {
		return key + " Armor Durability";
	}
	protected String reductionAmounts() {
		return key + " Armor Reduction Amount";
	}
	protected String enchantability() {
		return key + " Armor Enchantability";
	}

	@Override void load(Configuration configFile) {
		value = new ArmorStat(
				configFile.get(category, durability(), value.durability).getInt(value.durability),
				configFile.get(category, reductionAmounts(), value.reductionAmountString()).getString(),
				configFile.get(category, enchantability(), value.enchantability).getInt(value.enchantability)
		);
	}

	@Override void save(Configuration configFile) {
		configFile.get(category, durability(), 0).set(value.durability);
		configFile.get(category, reductionAmounts(), "").set(value.reductionAmountString());
		configFile.get(category, enchantability(), 0).set(value.enchantability);
		
		configFile.save();
	}

}
