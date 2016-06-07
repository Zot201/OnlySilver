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
package zotmc.onlysilver;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import zotmc.onlysilver.data.ModData.OnlySilvers;
import zotmc.onlysilver.util.Utils;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

class CommonProxy {

  private final Table<ResourceLocation, String, List<LootEntry>> lootEntries = HashBasedTable.create();

  CommonProxy() { }

  void registerItemModels(Block b, String... models) {
    registerItemModels(checkNotNull(Item.getItemFromBlock(b)), models);
  }

  public void registerItemModels(Item i, String... models) { }

  public <T extends Entity> void registerEntityRenderer(Class<T> entity) { }

  public void spawnEntityInWorld(Entity entity) {
    entity.worldObj.spawnEntityInWorld(entity);
  }

  void addLootEntry(ResourceLocation category, String pool, LootEntry entry) {
    List<LootEntry> list = lootEntries.get(category, pool);
    if (list == null) {
      lootEntries.put(category, pool, list = Lists.newArrayList());
    }
    list.add(entry);
  }


  @SubscribeEvent public void onItemCraft(ItemCraftedEvent event) {
    if (event.player != null && Contents.buildSilverBow.exists()) {
      ItemStack item = event.crafting;

      if (item != null && item.getItem() == ItemFeature.silverBow.get())
        event.player.addStat(Contents.buildSilverBow.get(), 1);
    }
  }

  @SubscribeEvent public void onItemExpire(ItemExpireEvent event) {
    if (CommonHooks.silverAuraExists) {
      ItemStack item = event.getEntityItem().getEntityItem();

      if (Utils.hasEnch(item, Contents.silverAura.get())) {
        if (item.getItem() == Items.ENCHANTED_BOOK) {
          event.setExtraLife(96000);
          event.setCanceled(true);
        }
        else {
          NBTTagCompound data = event.getEntityItem().getEntityData();

          if (!data.getBoolean(OnlySilvers.MODID + "-lifeExtended")) {
            event.setExtraLife(12000);
            event.setCanceled(true);
            data.setBoolean(OnlySilvers.MODID + "-lifeExtended", true);
          }
        }
      }
    }
  }

  @SubscribeEvent public void onEntityJoinWorld(EntityJoinWorldEvent event) {
    if (event.getEntity() instanceof EntityArrow) {
      Double extraDamage = CommonHooks.arrowLooseContext.get();

      if (extraDamage != null) {
        EntityArrow arrow = (EntityArrow) event.getEntity();

        try {
          arrow.setDamage(extraDamage + arrow.getDamage());
        }
        catch (Throwable t) {
          OnlySilver.INSTANCE.log.catching(t);
        }
      }
    }
  }

  @SubscribeEvent public void onLootTableLoad(LootTableLoadEvent event) {
    Map<String, List<LootEntry>> entries = lootEntries.row(event.getName());

    if (!entries.isEmpty()) {
      LootTable table = event.getTable();

      entries.entrySet().forEach(e -> {
        LootPool pool = table.getPool(e.getKey());

        //noinspection ConstantConditions
        if (pool != null) {
          e.getValue().forEach(pool::addEntry);
        }
        else {
          OnlySilver.INSTANCE.log.error("Missing pool %s in %s?", e.getKey(), event.getName());
        }
      });
    }
  }

}
