package zotmc.onlysilver.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static zotmc.onlysilver.config.Config.ConfigState.AVAILABLE;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import net.minecraftforge.common.config.Configuration;
import zotmc.onlysilver.block.BlockStat;
import zotmc.onlysilver.ench.EnchData;
import zotmc.onlysilver.item.ArmorStat;
import zotmc.onlysilver.item.ToolStat;
import zotmc.onlysilver.oregen.OreGenProfile;
import zotmc.onlysilver.util.Holder;
import zotmc.onlysilver.util.Utils;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

public class Config {

	private static final String
	GENERAL = "General Settings",
	ORE_GEN = "Ore Generation Settings",
	ENCH = "Enchantment Settings",
	
	BLOCK_STAT = "Block Stats",
	TOOL_STAT = "Tool Stats",
	ARMOR_STAT = "Armor Stats";
	

	public final Configurable<Boolean>
	enableWerewolf = new ConfigurableBoolean(GENERAL, "Silver Equips Do Extra Damage Vs Werewolf").set(true),
	enableAkka = new ConfigurableBoolean(GENERAL, "Enable 'Akkamaddi Style' Mob Equip Spawning").set(true),
	enableMeleeBow = new ConfigurableBoolean(GENERAL, "Enable silver bow with melee knockback").set(true);
	
	public final Configurable<OreGenProfile>
	oreGenProfile = new ConfigurableOreGen(ORE_GEN, "Ore Generation Profile", "Block Definitions")
		.set(new OreGenProfile(
				"\"stone_w of ALL\\{-1, 1} -> [0..42) = silver x 5 x 8\"",
				"\"stone_w = minecraft:stone@W\"",
				"\"silver = onlysilver:silverOre\""
		));
	
	public final Configurable<EnchData>
	everlasting = new ConfigurableEnch(ENCH, "Everlasting").set(new EnchData(true, 141)),
	incantation = new ConfigurableEnch(ENCH, "Incantation").set(new EnchData(true, 142));
	
	public final Configurable<BlockStat>
	silverOre = new ConfigurableBlockStat(BLOCK_STAT, "Silver Ore").set(new BlockStat(1, 3, 10)),
	silverBlock = new ConfigurableBlockStat(BLOCK_STAT, "Silver Block").set(new BlockStat(-1, 7, 12));
	
	public final Configurable<ToolStat>
	toolSilver = new ConfigurableToolStat(TOOL_STAT, "Silver").set(new ToolStat(2, 226, 8, 2, 30));
	
	public final Configurable<ArmorStat>
	armorSilver = new ConfigurableArmorStat(ARMOR_STAT, "Silver").set(new ArmorStat(15, 3, 5, 4, 3, 30));
	
	
	
	
	public enum ConfigState {
		INSPECTING,
		AVAILABLE;
	}
	
	Config() { }
	
	private static Configuration configFile;
	private static Holder<ConfigState> configState;
	
	private static Config preserved, current;
	private Config inspect;
	
	public static void init(Configuration configFile, Holder<ConfigState> configState) {
		if (Config.configFile != null)
			throw new IllegalStateException("Already initialized");
		
		Config.configFile = checkNotNull(configFile);
		Config.configState = checkNotNull(configState);
		
		current = new Config().load().save();
		configState.set(AVAILABLE);
		
	}
	
	static Config preserved() {
		return preserved != null ? preserved : (preserved = current.copy());
	}
	public static Config current() {
		return configState.get() == AVAILABLE ? current : current.inspect;
	}
	
	
	
	
	private static Iterable<Field> configurableFields() {
		return FluentIterable
				.from(Arrays.asList(Config.class.getDeclaredFields()))
				.filter(new Predicate<Field>() {
					@Override public boolean apply(Field input) {
						return !Modifier.isStatic(input.getModifiers())
								&& Configurable.class.isAssignableFrom(input.getType());
					}
				});
	}
	
	
	Config apply(Config config) {
		for (Field f : configurableFields())
			Utils.<Configurable<Object>>get(f, this).set(
					Utils.<Configurable<?>>get(f, config).get()
			);
		return this;
	}
	
	Config applyHot(Config config) {
		return apply(config); //TODO: stub
	}
	
	Config load() {
		for (Field f : configurableFields())
			Utils.<Configurable<?>>get(f, this).load(configFile);
		return this;
	}
	
	Config save() {
		for (Field f : configurableFields())
			Utils.<Configurable<?>>get(f, this).save(configFile);
		return this;
	}
	
	Config copy() {
		return new Config().apply(this);
	}
	
}
