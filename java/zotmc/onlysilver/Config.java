package zotmc.onlysilver;

import static zotmc.onlysilver.OnlySilver.NAME;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Level;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class Config {

	private static final String
	GENERAL_CAT = "General Settings",
	ORE_GEN_CAT = "Ore Generation Settings",
	ENCH_CAT = "Enchantment Settings",
	
	BLOCK_STAT_CAT = "Block Stats",
	TOOL_STAT_CAT = "Tool Stats",
	ARMOR_STAT_CAT = "Armor Stats";
	
	

	public static final Configurable<Boolean>
	werewolfEffectiveness	= ofBoolean(true, GENERAL_CAT, "Silver Equips Do Extra Damage Vs Werewolf"),
	silverBlockPrioritize	= ofBoolean(true, GENERAL_CAT, "Prioritize Silver Block Recipe",
			"Set this true if you find that you cannot create a silver golem with the silver blocks you have."),
	akkamaddiJoinWorld		= ofBoolean(true, GENERAL_CAT, "Enable 'Akkamaddi Style' Mob Equip Spawning"),
	enableBalkon			= ofBoolean(true, GENERAL_CAT, "Enable Balkon's Weapon Mod Add-on Items");
	
	public static final Configurable<String[]>
	oreGenerationProfile	= ofStringList(new String[] {
			"\"stone of ALL\\{-1, 1} -> [0..42) = silver x 5 x 8\""
			}, ORE_GEN_CAT, "Ore Generation Profile",
			"============================================================\n"
			+ "Basic Format:\n"
			+ "  \"BASE_BLOCK of DIMENSIONS -> HEIGHTS = ORE x VEIN_SIZE x SPAWN_RATE\"\n"
			+ "\n"
			+ "Notation for Dimensions and Heights:\n"
			+ "  '[' and ']' brackets represent close boundaries.\n"
			+ "  '(' and ')' brackets represent open boundaries.\n"
			+ "  '{' and '}' are used together to represent discrete elements.\n"
			+ "  '\\' represents an exclusion.\n"
			+ "  'ALL' (in ALL CAPS) can be used to represent the universal set.\n"
			+ "  '-INF' and '+INF' (in ALL CAPS) can be used to represent non-exist boundaries \n"
			+ "    e.g. (-INF..0] means negative infinity to zero (inclusive)\n"
			+ "============================================================"),
	blockDefinitions		= ofStringList(new String[] {
			"\"stone = minecraft:stone\"",
			"\"silver = onlysilver:silverOre\""
			}, ORE_GEN_CAT, "Block Definitions");
	
	public static final Configurable<Integer>
	everlastingID			= ofInt(141, ENCH_CAT, "Everlasting Enchantment ID"),
	incantationID			= ofInt(142, ENCH_CAT, "Incantation Enchantment ID");
	public static final Configurable<Boolean>
	enableEverlasting		= ofBoolean(true, ENCH_CAT, "Enable Everlasting Enchantment"),
	enableIncantation		= ofBoolean(true, ENCH_CAT, "Enable Incantation Enchantment");
	
	
	public static final Configurable<Integer>
	silverOreHarvestLevel	= ofInt(1, BLOCK_STAT_CAT, "Silver Ore Harvest Level");
	public static final Configurable<Float>
	silverOreHardness		= ofFloat(3, BLOCK_STAT_CAT, "Silver Ore Hardness"),
	silverOreResistance		= ofFloat(10, BLOCK_STAT_CAT, "Silver Ore Resistance"),
	silverBlockHardness		= ofFloat(7, BLOCK_STAT_CAT, "Silver Block Hardness"),
	silverBlockResistance	= ofFloat(12, BLOCK_STAT_CAT, "Silver Block Resistance");

	public static final Configurable<Integer>
	silverHarvestLevel		= ofInt(2, TOOL_STAT_CAT, "Silver Tool Harvest level"),
	silverMaxUses			= ofInt(226, TOOL_STAT_CAT, "Silver Tool Max Uses");
	public static final Configurable<Float>
	silverEfficiency		= ofFloat(8, TOOL_STAT_CAT, "Silver Tool Efficiency"),
	silverDamage			= ofFloat(2, TOOL_STAT_CAT, "Silver Tool Damage");
	public static final Configurable<Integer>
	silverEnchant			= ofInt(30, TOOL_STAT_CAT, "Silver Tool Enchantability");
	
	public static final Configurable<Integer>
	silverArmorDurability	= ofInt(15, ARMOR_STAT_CAT, "Silver Armor Durability");
	public static final Configurable<int[]>
	silverArmorReduction	= ofString("3, 5, 4, 3", ARMOR_STAT_CAT, "Silver Armor Reduction Amount").toIntList();
	public static final Configurable<Integer>
	silverArmorEnchant		= ofInt(30, ARMOR_STAT_CAT, "Silver Armor Enchantability");
	
	
	
	
	public static void init(FMLPreInitializationEvent event) {
		Configuration c = new Configuration(event.getSuggestedConfigurationFile());
		try {
			loadConfiguration(c,
					werewolfEffectiveness,
					silverBlockPrioritize,
					akkamaddiJoinWorld,
					//enableBalkon,
					
					oreGenerationProfile,
					blockDefinitions,
					
					everlastingID,
					incantationID,
					
					enableEverlasting,
					enableIncantation,
					
					silverOreHarvestLevel,
					silverOreHardness,
					silverOreResistance,
					silverBlockHardness,
					silverBlockResistance,
					
					silverHarvestLevel,
					silverMaxUses,
					silverEfficiency,
					silverDamage,
					silverEnchant,
					
					silverArmorDurability,
					silverArmorReduction,
					silverArmorEnchant
					);

		} catch (Exception e) {
			FMLLog.log(Level.ERROR, e, "[" + NAME + "] Error while trying to access the "
					+ NAME + " config file!");
			
		} finally {
			if (c.hasChanged())
				c.save();
			System.out.println("[" + NAME + "] Config loaded.");
		}
	}
	
	protected static void loadConfiguration(Configuration configuration, Configurable<?>... configs) {
		for (Configurable<?> config : configs)
			config.load(configuration);
	}
	
	
	
	
	
	public abstract static class Configurable<E> {
		protected E value;
		private boolean isLoaded;
		protected final String category, key, comment;
		protected Configurable(E defaultValue, String category, String key, String comment) {
			this.value = defaultValue;
			this.category = category;
			this.key = key;
			this.comment = comment;
		}
		public E get() {
			if (!isLoaded)
				throw new IllegalStateException();
			return value;
		}
		protected void load(Configuration c) {
			isLoaded = true;
		}
		protected void setLoaded() {
			isLoaded = true;
		}
	}
	
	protected static Configurable<Boolean> ofBoolean(boolean defaultValue, String category, String key) {
		return ofBoolean(defaultValue, category, key, null);
	}
	protected static Configurable<Boolean> ofBoolean(boolean defaultValue, String category, String key, String comment) {
		return new Configurable<Boolean>(defaultValue, category, key, comment) {
			@Override protected void load(Configuration c) {
				super.load(c);
				value = c.get(category, key, value, comment).getBoolean(value);
			}
		};
	}

	protected static Configurable<Integer> ofInt(int defaultValue, String category, String key) {
		return ofInt(defaultValue, category, key, null);
	}
	protected static Configurable<Integer> ofInt(int defaultValue, String category, String key, String comment) {
		return new Configurable<Integer>(defaultValue, category, key, comment) {
			@Override protected void load(Configuration c) {
				super.load(c);
				value = c.get(category, key, value, comment).getInt(value);
			}
		};
	}

	protected static Configurable<Float> ofFloat(float defaultValue, String category, String key) {
		return ofFloat(defaultValue, category, key, null);
	}
	protected static Configurable<Float> ofFloat(float defaultValue, String category, String key, String comment) {
		return new Configurable<Float>(defaultValue, category, key, comment) {
			@Override protected void load(Configuration c) {
				super.load(c);
				value = (float) c.get(category, key, value, comment).getDouble(value);
			}
		};
	}
	
	protected static class StringConfigurable extends Configurable<String> {
		public StringConfigurable(String defaultValue, String category, String key, String comment) {
			super(defaultValue, category, key, comment);
		}
		@Override protected void load(Configuration c) {
			super.load(c);
			value = c.get(category, key, value, comment).getString();
		}
		
		private static final Splitter splitter = Splitter.on(',').trimResults();
		private static final Function<String, Integer> parseInt = new Function<String, Integer>() {
			@Override public Integer apply(String input) {
				return Integer.parseInt(input);
			}
		};
		public Configurable<int[]> toIntList() {
			return new Configurable<int[]>(null, category, key, comment) {
				@Override protected void load(Configuration c) {
					StringConfigurable.this.load(c);
				}
				@Override public int[] get() {
					return Ints.toArray(Lists.transform(
							splitter.splitToList(StringConfigurable.this.get()), parseInt));
				}
			};
		}
	}
	
	protected static StringConfigurable ofString(String defaultValue, String category, String key) {
		return ofString(defaultValue, category, key, null);
	}
	protected static StringConfigurable ofString(String defaultValue, String category, String key, String comment) {
		return new StringConfigurable(defaultValue, category, key, comment);
	}
	
	protected static Configurable<String[]> ofStringList(String[] defaultValue, String category, String key) {
		return ofStringList(defaultValue, category, key, null);
	}
	protected static Configurable<String[]> ofStringList(String[] defaultValue, String category, String key, String comment) {
		return new Configurable<String[]>(defaultValue, category, key, comment) {
			@Override protected void load(Configuration c) {
				super.load(c);
				value = c.get(category, key, value, comment).getStringList();
			}
		};
	}
	
}