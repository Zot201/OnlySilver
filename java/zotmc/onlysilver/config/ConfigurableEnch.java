package zotmc.onlysilver.config;

import net.minecraftforge.common.config.Configuration;
import zotmc.onlysilver.ench.EnchData;

public class ConfigurableEnch extends Configurable<EnchData> {
	
	private EnchData value = new EnchData();

	public ConfigurableEnch(String category, String enchName) {
		super(category, enchName);
	}

	@Override public EnchData get() {
		return value;
	}

	@Override Configurable<EnchData> set(EnchData value) {
		this.value = value;
		return this;
	}
	
	protected String isEnabled() {
		return String.format("Enable %s Enchantment", key);
	}
	protected String enchId() {
		return String.format("%s Enchantment ID", key);
	}

	@Override void load(Configuration configFile) {
		value = new EnchData(
				configFile.get(category, isEnabled(), value.isEnabled).getBoolean(value.isEnabled),
				configFile.get(category, enchId(), value.enchId).getInt(value.enchId)
		);
	}

	@Override void save(Configuration configFile) {
		configFile.get(category, isEnabled(), false).set(value.isEnabled);
		configFile.get(category, enchId(), 0).set(value.enchId);
		
		configFile.save();
	}

}
