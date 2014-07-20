package zotmc.onlysilver.config;

import net.minecraftforge.common.config.Configuration;
import zotmc.onlysilver.oregen.OreGenProfile;

public class ConfigurableOreGen extends Configurable<OreGenProfile> {
	
	private final String blockDefKey;
	private OreGenProfile value = new OreGenProfile();

	public ConfigurableOreGen(String category, String key, String blockDefKey) {
		super(category, key);
		this.blockDefKey = blockDefKey;
	}

	@Override public OreGenProfile get() {
		return value;
	}

	@Override Configurable<OreGenProfile> set(OreGenProfile value) {
		this.value = value;
		return this;
	}

	@Override void load(Configuration configFile) {
		String[] a = configFile.get(category, key, value.oreGenStrings()).getStringList();
		String[] b = configFile.get(category, blockDefKey, value.blockDefStrings()).getStringList();
		
		value = new OreGenProfile(a, b);
	}

	@Override void save(Configuration configFile) {
		configFile.get(category, key, new String[0]).set(value.oreGenStrings());
		configFile.get(category, blockDefKey, new String[0]).set(value.blockDefStrings());
		
		configFile.save();
	}

}
