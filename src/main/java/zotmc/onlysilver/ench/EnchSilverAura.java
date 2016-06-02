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
package zotmc.onlysilver.ench;

import java.util.Random;
import java.util.UUID;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import zotmc.onlysilver.CommonHooks;
import zotmc.onlysilver.Contents;
import zotmc.onlysilver.api.OnlySilverUtils;
import zotmc.onlysilver.data.ReflData;
import zotmc.onlysilver.util.Fields;
import zotmc.onlysilver.util.Utils;

import com.google.common.collect.Multimap;

public class EnchSilverAura extends Enchantment {

  private static final String ATTACK_DAMAGE = SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName();
  private static final UUID weaponModifierUUID = Fields.get(null, ReflData.ITEM_MODIFIER_UUID);
  private final ThreadLocal<Boolean> lock = Utils.newThreadLocal(false);

  public EnchSilverAura(ResourceLocation uid) {
    super(Rarity.UNCOMMON, Contents.BREAKABLE, new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND});
    setRegistryName(uid);
  }

  @Override public int getMinLevel() {
    return 1;
  }

  @Override public int getMaxLevel() {
    return 1;
  }

  @Override public int getMinEnchantability(int lvl) {
    return 5 + 20 * (lvl - 1);
  }

  @Override public int getMaxEnchantability(int lvl) {
    return super.getMaxEnchantability(lvl) + 50;
  }

  @Override public boolean canApply(ItemStack item) {
    return super.canApplyAtEnchantingTable(item);
  }

  @Override public boolean canApplyAtEnchantingTable(ItemStack item) {
    return (CommonHooks.enchantingContext.get() || OnlySilverUtils.isSilverEquip(item)) && super.canApplyAtEnchantingTable(item);
  }

  @Override public boolean isAllowedOnBooks() {
    return false;
  }

  @SuppressWarnings("unchecked")
  private static Multimap<String, AttributeModifier> getAttributeModifiers(ItemStack item) {
    return item.getItem().getAttributeModifiers(item);
  }

  /**
   * @see ItemStack#getTooltip
   */
  private static boolean hasWeaponModifier(ItemStack item) {
    for (AttributeModifier modifier : getAttributeModifiers(item).get(ATTACK_DAMAGE))
      if (modifier.getID() == weaponModifierUUID) // identical instance is guaranteed by vanilla usage (1.8)
        return true;
    return false;
  }

  @Override public float calcDamageByCreature(int lvl, EnumCreatureAttribute attribute) {
    if (!lock.get()) {
      ItemStack item = CommonHooks.modifierContext.get();

      if (item != null && item.getItem() != null) {
        lock.set(true);
        float ret = 0;

        try {
          if (hasWeaponModifier(item)) {
            int durability = 1 + Math.max(0, item.getMaxDamage());
            float enchantability = Math.max(1, 2.5F + (item.getItem().getItemEnchantability(item) / 4.0F));
            ret = 2 * lvl * enchantability / (float) Math.cbrt(durability);
          }

        } finally {
          lock.set(false);
        }
        return ret;
      }
    }
    return 0;
  }

  @Override public int calcModifierDamage(int lvl, DamageSource source) {
    if (!lock.get()) {
      ItemStack item = CommonHooks.modifierContext.get();

      if (item != null && item.getItem() != null) {
        lock.set(true);
        float f = 0;

        try {
          int durability = 1 + Math.max(0, item.getMaxDamage());
          float enchantability = Math.max(1, 2.5F + (item.getItem().getItemEnchantability(item) / 4.0F));
          f = 1.5F * lvl * enchantability / (float) Math.cbrt(durability);

        } finally {
          lock.set(false);
        }
        return (int) (0.25F * (6 + f * f));
      }
    }
    return 0;
  }

  public int getAuraEfficiency(ItemStack item) {
    if (!lock.get() && item.getItem() != null) {
      int lvl = Utils.getEnchLevel(item, this);

      if (lvl > 0) {
        lock.set(true);
        int ret = 0;

        try {
          int durability = 1 + Math.max(0, item.getMaxDamage());
          float enchantability = Math.max(1, 2.5F + (item.getItem().getItemEnchantability(item) / 4.0F));
          ret = (int) (lvl * enchantability / (float) Math.cbrt(durability));

        } finally {
          lock.set(false);
        }
        return ret;
      }
    }
    return 0;
  }

  public boolean negateDamage(ItemStack item, Random rand) {
    if (!lock.get() && item.getItem() != null) {
      int lvl = Utils.getEnchLevel(item, this);

      if (lvl > 0 && (!(item.getItem() instanceof ItemArmor) || rand.nextFloat() < 0.4F)) {
        lock.set(true);
        boolean ret = false;

        try {
          int durability = 1 + Math.max(0, item.getMaxDamage());
          float enchantability = Math.max(1, 2.5F + (item.getItem().getItemEnchantability(item) / 4.0F));
          ret = rand.nextFloat() < 1 / (1 + 2 * (float) Math.cbrt(durability) / (lvl * enchantability));

        } finally {
          lock.set(false);
        }
        return ret;
      }
    }
    return false;
  }

  public Double getAuraArrowDamage(ItemStack item) {
    if (!lock.get() && item.getItem() != null) {
      int lvl = Utils.getEnchLevel(item, this);

      if (lvl > 0) {
        lock.set(true);
        double f = 0;

        try {
          int durability = 1 + Math.max(0, item.getMaxDamage());
          float enchantability = Math.max(1, 2.5F + (item.getItem().getItemEnchantability(item) / 4.0F));
          f = 1.6 * lvl * enchantability / Math.cbrt(durability);

        } finally {
          lock.set(false);
        }
        return f + 0.5;
      }
    }
    return null;
  }

}
