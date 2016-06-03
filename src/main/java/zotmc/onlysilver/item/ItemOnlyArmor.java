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

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import zotmc.onlysilver.CommonHooks;

public class ItemOnlyArmor extends ItemArmor {

  public ItemOnlyArmor(ArmorMaterial armorMaterial, EntityEquipmentSlot armorType) {
    super(armorMaterial, -1, armorType);
  }

  @Override public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
    return CommonHooks.isSilverIngot(repair);
  }

}
