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
package zotmc.onlysilver.data;

import zotmc.onlysilver.util.Utils;
import zotmc.onlysilver.util.Utils.Localization;

public class LangData {

  public static final Localization
  KNOCKBACK_TOOLTIP = Utils.localize("onlysilver.tooltip.knockback"),

  SILVER_ORE_ONLY_SILVER = Utils.localize("onlysilver.customize.silverOre", "tile.silverOre.name", "itemGroup.tabOnlySilver"),

  SIZE = Utils.localize("createWorld.customize.custom.size"),
  COUNT = Utils.localize("createWorld.customize.custom.count"),
  MIN_HEIGHT = Utils.localize("createWorld.customize.custom.minHeight"),
  MAX_HEIGHT = Utils.localize("createWorld.customize.custom.maxHeight"),

  ONLYSILVER_OPTIONS = Utils.localize("onlysilver.options.title", "itemGroup.tabOnlySilver", "options.title"),

  ORE_GENERATION = Utils.localize("onlysilver.options.oreGeneration"),
  SILVER_GEN_DEFAULTS = Utils.localize("onlysilver.options.silverGenDefaults"),
  DIMENSIONS = Utils.localize("onlysilver.options.dimensions"),

  ENCHANTMENTS = Utils.localize("onlysilver.options.enchantments"),
  SILVER_AURA = Utils.localize("enchantment.onlysilver.silverAura"),
  INCANTATION = Utils.localize("enchantment.onlysilver.incantation"),

  STATS = Utils.localize("onlysilver.options.stats"),
  HARVEST_LEVEL = Utils.localize("onlysilver.options.harvestLevel"),
  HARDNESS = Utils.localize("onlysilver.options.hardness"),
  RESISTANCE = Utils.localize("onlysilver.options.resistance"),
  SILVER_TOOLS = Utils.localize("onlysilver.options.silverTools"),
  MAX_USES = Utils.localize("onlysilver.options.maxUses"),
  EFFICIENCY = Utils.localize("onlysilver.options.efficiency"),
  DAMAGE = Utils.localize("onlysilver.options.damage"),
  ENCHANTABILITY = Utils.localize("onlysilver.options.enchantability"),
  TOUGHNESS = Utils.localize("onlysilver.options.toughness"),
  SILVER_ARMOR = Utils.localize("onlysilver.options.silverArmor"),
  DURABILITY = Utils.localize("onlysilver.options.durability"),
  HELMET_DEFENSE_POINT = Utils.localize("onlysilver.options.helmetDefensePoint"),
  CHESTPLATE_DEFENSE_POINT = Utils.localize("onlysilver.options.chestplateDefensePoint"),
  LEGGINGS_DEFENSE_POINT = Utils.localize("onlysilver.options.leggingsDefensePoint"),
  BOOTS_DEFENSE_POINT = Utils.localize("onlysilver.options.bootsDefensePoint"),

  MISCELLANEOUS = Utils.localize("key.categories.misc"),
  OPTIONAL_FEATURES = Utils.localize("onlysilver.options.optionalFeatures"),
  MELEE_BOW_KNOCKBACK = Utils.localize("onlysilver.options.meleeBowKnockback"),
  WEREWOLF_EFFECTIVENESS = Utils.localize("onlysilver.options.werewolfEffectiveness"),
  SILVER_GOLEM_ASSEMBLY = Utils.localize("onlysilver.options.silverGolemAssembly"),

  DEFAULT = Utils.localize("onlysilver.options.default", "generator.default"),
  ON = Utils.localize("options.on"),
  OFF = Utils.localize("options.off"),
  EDIT = Utils.localize("selectServer.edit"),
  ENABLED = Utils.localize("addServer.resourcePack.enabled"),
  DISABLED = Utils.localize("addServer.resourcePack.disabled"),

  RESET_TO_DEFAULT = Utils.localize("fml.configgui.tooltip.resetToDefault"),

  DONE = Utils.localize("gui.done"),
  CANCEL = Utils.localize("gui.cancel");

}
