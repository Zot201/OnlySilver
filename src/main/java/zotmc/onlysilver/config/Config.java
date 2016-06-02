package zotmc.onlysilver.config;

import java.io.File;
import java.util.Set;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import zotmc.onlysilver.util.RawTypeAdapterFactory;

import com.google.common.collect.ImmutableSet;
import com.google.gson.GsonBuilder;

public class Config extends AbstractConfig<Config> {

  // oregen
  @Local public final Property<GenDefaults>
  silverGenDefaults = base(new GenDefaults(null, 5, 8, 0, 42));

  // enchantment ids
  @Restart public final Property<Integer>
  silverAuraId = base(141),
  incantationId = base(142);

  // stats
  @Restart public final Property<BlockStats>
  silverOreStats = base(new BlockStats(1, 3, 10)),
  silverBlockStats = base(new BlockStats(-1, 7, 12));

  @Restart public final Property<ToolStats>
  silverToolStats = base(new ToolStats(2, 226, 8, 2, 30));

  @Restart public final Property<ArmorStats>
  silverArmorStats = base(new ArmorStats(15, 3, 5, 4, 3, 30));

  // misc
  @Restart public final Property<Set<String>>
  disabledFeatures = base(ImmutableSet.<String>of());

  public final Property<Boolean>
  meleeBowKnockback = base(true),
  werewolfEffectiveness = base(true),
  silverGolemAssembly = base(true);


  @Instances private static final Config[] instances = {};

  private Config() { }

  public static void init(String name, EventBus bus, File file) {
    new Config().initConfig(name, bus, file);
  }

  public static Config current() {
    return instances[CURRENT];
  }

  static Config inFile() {
    return instances[IN_FILE];
  }

  @Override protected GsonBuilder getGsonBuilder() {
    return new GsonBuilder()
        .registerTypeAdapterFactory(new ArmorStats.AdapterFactory())
        .registerTypeAdapterFactory(RawTypeAdapterFactory.immutableSet());
  }

}
