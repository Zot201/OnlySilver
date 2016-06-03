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

import net.minecraft.block.Block;

import javax.annotation.Nullable;

public final class BlockStats {

  final int harvestLevel;
  final float hardness, resistance;

  BlockStats(int harvestLevel, float hardness, float resistance) {
    this.harvestLevel = harvestLevel;
    this.hardness = hardness;
    this.resistance = resistance;
  }

  public void setStatTo(Block block, @Nullable String toolClass) {
    block.setHardness(hardness);
    block.setResistance(resistance);
    if (toolClass != null) block.setHarvestLevel(toolClass, harvestLevel);
  }

  @Override public int hashCode() {
    return Objects.hash(harvestLevel, hardness, resistance);
  }

  @Override public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj instanceof BlockStats) {
      BlockStats o = (BlockStats) obj;
      return harvestLevel == o.harvestLevel && hardness == o.hardness && resistance == o.resistance;
    }
    return false;
  }

}
