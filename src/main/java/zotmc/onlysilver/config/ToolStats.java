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

import java.util.Objects;

import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;

public final class ToolStats {

  final int harvestLevel, maxUses;
  final float efficiency, damage;
  final int enchantability;

  ToolStats(int harvestLevel, int maxUses, float efficiency, float damage, int enchantability) {
    this.harvestLevel = harvestLevel;
    this.maxUses = maxUses;
    this.efficiency = efficiency;
    this.damage = damage;
    this.enchantability = enchantability;
  }

  public ToolMaterial addToolMaterial(String name) {
    return EnumHelper.addToolMaterial(name, harvestLevel, maxUses, efficiency, damage, enchantability);
  }

  @Override public int hashCode() {
    return Objects.hash(harvestLevel, maxUses, efficiency, damage, enchantability);
  }

  @Override public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj instanceof ToolStats) {
      ToolStats o = (ToolStats) obj;
      return harvestLevel == o.harvestLevel && maxUses == o.maxUses && efficiency == o.efficiency
          && damage == o.damage && enchantability == o.enchantability;
    }
    return false;
  }

}
