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
package zotmc.onlysilver.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import java.util.Random;

public class AttackItem {

  private final DamageSourceHandler handler;
  private final DamageSource damage;
  private ItemStack cachedItem;

  AttackItem(DamageSourceHandler handler, DamageSource damage, ItemStack cachedItem) {
    this.handler = handler;
    this.damage = damage;
    this.cachedItem = cachedItem;
  }

  public ItemStack getItem() {
    return cachedItem != null ? cachedItem : (cachedItem = handler.getItem(damage));
  }

  @SuppressWarnings("WeakerAccess")
  public void updateItem(ItemStack item) {
    handler.updateItem(damage, item);
    cachedItem = null;
  }

  public EntityLivingBase getAttacker() {
    Entity ret = damage.getEntity();
    return ret instanceof EntityLivingBase ? (EntityLivingBase) ret : null;
  }

  public int getEnchantmentLevel(Enchantment ench) {
    ItemStack item = getItem();
    return item == null ? 0 : EnchantmentHelper.getEnchantmentLevel(ench, item);
  }

  public void damageItem(int amount, Random rand) {
    EntityLivingBase attacker = getAttacker();
    if (attacker != null) getItem().damageItem(amount, attacker);
    else getItem().attemptDamageItem(amount, rand);
  }

  public boolean isItemRunOut() {
    ItemStack item = getItem();
    return item == null || item.stackSize <= 0;
  }

  public void updateItemUponConsumption() {
    ItemStack item = getItem();
    if (item != null && item.stackSize <= 0) item = null;
    updateItem(item);
  }

}
