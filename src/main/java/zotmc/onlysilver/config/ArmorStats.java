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

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;
import zotmc.onlysilver.util.RawTypeAdapterFactory;

import java.util.List;
import java.util.Objects;

public final class ArmorStats {

  final int durability;
  final List<Integer> reductionAmounts;
  final int  enchantability;
  final float toughness;

  ArmorStats(int durability, int r0, int r1, int r2, int r3, int enchantability, float toughness) {
    this(durability, ImmutableList.of(r0, r1, r2, r3), enchantability, toughness);
  }
  private ArmorStats(int durability, List<Integer> reductionAmounts, int enchantability, float toughness) {
    this.durability = durability;
    this.reductionAmounts = reductionAmounts;
    this.enchantability = enchantability;
    this.toughness = toughness;
  }

  public ArmorMaterial addArmorMaterial(String name, String rendererPrefix) {
    return EnumHelper.addArmorMaterial(name, rendererPrefix, durability, Ints.toArray(reductionAmounts), enchantability,
        SoundEvents.ITEM_ARMOR_EQUIP_GOLD, toughness);
  }

  @Override public int hashCode() {
    return Objects.hash(durability, reductionAmounts, enchantability);
  }

  @Override public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj instanceof ArmorStats) {
      ArmorStats o = (ArmorStats) obj;
      return durability == o.durability && enchantability == o.enchantability && toughness == o.toughness
          && reductionAmounts.equals(o.reductionAmounts);
    }
    return false;
  }


  static class AdapterFactory extends RawTypeAdapterFactory<ArmorStats> {
    @Override protected Class<? super ArmorStats> targetType() {
      return ArmorStats.class;
    }
    @Override protected ArmorStats postProcessing(ArmorStats in) {
      //noinspection ConstantConditions
      if (in != null) {
        List<?> list = in.reductionAmounts;
        //noinspection ConstantConditions,StaticPseudoFunctionalStyleMethod,Guava
        if (list == null || list.size() != 4 || Iterables.any(list, Predicates.isNull())) return null;
        return new ArmorStats(in.durability, ImmutableList.copyOf(in.reductionAmounts), in.enchantability, in.toughness);
      }
      return in;
    }
  }

}
