package zotmc.onlysilver;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import zotmc.onlysilver.data.ModData.OnlySilvers;
import zotmc.onlysilver.util.Utils;

public class CommonProxy {

  CommonProxy() { }

  public void registerItemModels(Block b, String... models) {
    registerItemModels(Item.getItemFromBlock(b), models);
  }

  public void registerItemModels(Item i, String... models) { }

  public void registerEntityRenderer(Class<? extends Entity> entity) { }

  public void spawnEntityInWorld(Entity entity) {
    entity.worldObj.spawnEntityInWorld(entity);
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
      ItemStack item = event.entityItem.getEntityItem();

      if (Utils.hasEnch(item, Contents.silverAura.get())) {
        if (item.getItem() == Items.enchanted_book) {
          event.extraLife = 96000;
          event.setCanceled(true);
        }
        else {
          NBTTagCompound data = event.entityItem.getEntityData();

          if (!data.getBoolean(OnlySilvers.MODID + "-lifeExtended")) {
            event.extraLife = 12000;
            event.setCanceled(true);
            data.setBoolean(OnlySilvers.MODID + "-lifeExtended", true);
          }
        }
      }
    }
  }

  @SubscribeEvent public void onEntityJoinWorld(EntityJoinWorldEvent event) {
    if (event.entity instanceof EntityArrow) {
      Double extraDamage = CommonHooks.arrowLooseContext.get();

      if (extraDamage != null) {
        EntityArrow arrow = (EntityArrow) event.entity;

        try {
          arrow.setDamage(extraDamage + arrow.getDamage());

        } catch (Throwable t) {
          OnlySilver.INSTANCE.log.catching(t);
        }
      }
    }
  }

}
