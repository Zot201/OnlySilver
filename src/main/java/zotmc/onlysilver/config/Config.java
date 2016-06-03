/*
 * Copyright 2016 Zot201
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

  // enchantments
  @Restart public final Property<Boolean>
  silverAuraEnabled = base(true),
  incantationEnabled = base(true);

  // stats
  @Restart public final Property<BlockStats>
  silverOreStats = base(new BlockStats(1, 3, 10)),
  silverBlockStats = base(new BlockStats(-1, 7, 12));

  @Restart public final Property<ToolStats>
  silverToolStats = base(new ToolStats(2, 226, 8, 2, 30));

  @Restart public final Property<ArmorStats>
  silverArmorStats = base(new ArmorStats(15, 3, 5, 4, 3, 30, 0));

  // misc
  @Restart public final Property<Set<String>>
  disabledFeatures = base(ImmutableSet.of());

  public final Property<Boolean>
  meleeBowKnockback = base(true),
  werewolfEffectiveness = base(true),
  silverGolemAssembly = base(true);


  @SuppressWarnings("MismatchedReadAndWriteOfArray") // Substituted reflectively
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
