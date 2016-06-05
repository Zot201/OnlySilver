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
package zotmc.onlysilver.item;

import net.minecraft.item.ItemAxe;
import zotmc.onlysilver.util.Utils;

import java.math.RoundingMode;

public class ItemOnlyAxe extends ItemAxe {

  public ItemOnlyAxe(ToolMaterial material) {
    super(material, getAttackDamage(material), getAttackSpeed(material));
  }

  private static float getAttackDamage(ToolMaterial m) {
    return 2 * (int) Math.rint(0.5 * (m.getDamageVsEntity() * 0.7058823529 + 6.352941176));
  }

  private static float getAttackSpeed(ToolMaterial m) {
    return Utils.roundToFloatDecimal(
        (m.getHarvestLevel() + m.getEfficiencyOnProperMaterial()) * 0.02312138728 - 3.275722543,
        RoundingMode.HALF_EVEN, 1);
  }

}
