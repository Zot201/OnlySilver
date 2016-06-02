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

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import zotmc.onlysilver.Contents;
import zotmc.onlysilver.api.AttackItem;
import zotmc.onlysilver.api.OnlySilverUtils;

import java.util.Random;

public class EnchIncantation extends Enchantment {

  public EnchIncantation(ResourceLocation uid) {
    super(Rarity.RARE, Contents.TOOL, new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND});
    setRegistryName(uid);
  }

  public EnchIncantation subscribeEvent() {
    MinecraftForge.EVENT_BUS.register(this);
    return this;
  }

  @Override public int getMinLevel() {
    return 1;
  }

  @Override public int getMaxLevel() {
    return 2;
  }

  @Override public int getMinEnchantability(int lvl) {
    return 15 + 9 * (lvl - 1);
  }

  @Override public int getMaxEnchantability(int lvl) {
    return super.getMaxEnchantability(lvl) + 50;
  }

  @Override public boolean canApplyAtEnchantingTable(ItemStack item) {
    return OnlySilverUtils.isSilverEquip(item) && super.canApplyAtEnchantingTable(item);
  }

  @Override public boolean isAllowedOnBooks() {
    return false;
  }


  @SubscribeEvent public void onLivingDrop(LivingDropsEvent event) {
    if (!event.entity.worldObj.isRemote) {
      AttackItem attackItem = OnlySilverUtils.getAttackItem(event.source);

      // TODO: Consider widen the range of attacker to mobs
      if (attackItem != null && attackItem.getAttacker() instanceof EntityPlayer) {
        int lvl = attackItem.getEnchantmentLevel(this);

        if (lvl > 0) {
          int strength = lvl * 10 - 5;
          boolean changed = false;

          for (EntityItem ei : event.drops) {
            ItemStack item = ei.getEntityItem();

            if (item != null && !item.isItemEnchanted() && item.isItemEnchantable()) {
              Random rand = event.entityLiving.getRNG();
              EnchantmentHelper.addRandomEnchantment(rand, item, strength);
              ei.setEntityItemStack(item);

              attackItem.damageItem(strength * 2, rand);
              changed = true;

              if (attackItem.isItemRunOut()) break;
            }
          }

          if (changed) attackItem.updateItemUponConsumption();
        }
      }
    }
  }

}
