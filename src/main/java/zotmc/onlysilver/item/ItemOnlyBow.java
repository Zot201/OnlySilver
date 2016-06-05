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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import zotmc.onlysilver.config.Config;
import zotmc.onlysilver.data.LangData;
import zotmc.onlysilver.data.ModData.OnlySilvers;
import zotmc.onlysilver.util.Utils;

import java.util.List;

public class ItemOnlyBow extends ItemBow {

  /*@SideOnly(Side.CLIENT)
  private static class Models {
    static final ModelResourceLocation[] inventoryModels = {
      new ModelResourceLocation(new ResourceLocation(OnlySilvers.MODID, "silver_bow_pulling_0"), "inventory"),
      new ModelResourceLocation(new ResourceLocation(OnlySilvers.MODID, "silver_bow_pulling_1"), "inventory"),
      new ModelResourceLocation(new ResourceLocation(OnlySilvers.MODID, "silver_bow_pulling_2"), "inventory")
    };
  }*/

  public static final String ARROW_FX = OnlySilvers.MODID + "-arrowFx";
  private final ToolMaterial material;

  public ItemOnlyBow(ToolMaterial material) {
    this.material = material;
    setFull3D();
    setMaxDamage(material.getMaxUses() * 2 + 1);
  }

  // TODO: Re-implement model overriding
  /*@SideOnly(Side.CLIENT)
  @Override public ModelResourceLocation getModel(ItemStack item, EntityPlayer player, int useRemaining) {
    if (player.getItemInUse() != null) {
      if (useRemaining >= 18) return Models.inventoryModels[2];
      if (useRemaining > 13) return Models.inventoryModels[1];
      if (useRemaining > 0) return Models.inventoryModels[0];
    }
    return null;
  }*/

  @Override public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4) {
    list.add(LangData.KNOCKBACK_TOOLTIP.get());
  }

  @Override public void onPlayerStoppedUsing(ItemStack item, World world, EntityLivingBase player, int timeLeft) {
    if (player instanceof EntityPlayer) {
      onPlayerStoppedUsing(item, world, (EntityPlayer) player, timeLeft);
    }
  }

  private ItemStack findAmmo(EntityPlayer player) {
    ItemStack item = player.getHeldItem(EnumHand.OFF_HAND);
    if (isArrow(item)) {
      return item;
    }
    item = player.getHeldItem(EnumHand.MAIN_HAND);
    if (isArrow(item)) {
      return item;
    }
    for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
      item = player.inventory.getStackInSlot(i);
      if (isArrow(item)) {
        return item;
      }
    }
    return null;
  }

  private void onPlayerStoppedUsing(ItemStack bow, World world, EntityPlayer player, int timeLeft) {
    boolean canShoot = player.capabilities.isCreativeMode || Utils.getEnchLevel(bow, Enchantments.INFINITY) > 0;
    ItemStack arrow = findAmmo(player);
    canShoot |= arrow != null;

    int charge = getMaxItemUseDuration(bow) - timeLeft;
    charge = ForgeEventFactory.onArrowLoose(bow, world, player, charge, canShoot);

    if (canShoot && charge >= 0) {
      if (arrow == null) {
        arrow = new ItemStack(Items.ARROW);
      }

      float f = getArrowVelocity(charge);

      if (f >= 0.1) {
        boolean infinity = player.capabilities.isCreativeMode
            || (arrow.getItem() instanceof ItemArrow && ((ItemArrow) arrow.getItem()).isInfinite(arrow, bow, player));

        if (!world.isRemote) {
          ItemArrow i = (ItemArrow) (arrow.getItem() instanceof ItemArrow ? arrow.getItem() : Items.ARROW);
          EntityArrow entity = i.createArrow(world, arrow, player);
          entity.setAim(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 3.0F, 1.0F);
          entity.getEntityData().setBoolean(ARROW_FX, true);

          if (f == 1) {
            entity.setIsCritical(true);
          }

          int power = Utils.getEnchLevel(bow, Enchantments.POWER);
          if (power > 0) {
            entity.setDamage(entity.getDamage() + power * 0.5 + 0.5);
          }

          entity.setKnockbackStrength(Utils.getEnchLevel(bow, Enchantments.PUNCH) + 2); // +2 for silver bow

          if (Utils.getEnchLevel(bow, Enchantments.FLAME) > 0) {
            entity.setFire(100);
          }

          bow.damageItem(1, player);

          if (infinity) {
            entity.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
          }

          world.spawnEntityInWorld(entity);
        }

        world.playSound(null, player.posX, player.posY, player.posZ,
            SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1,
            1 / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

        if (!infinity) {
          arrow.stackSize--;

          if (arrow.stackSize == 0) {
            player.inventory.deleteStack(arrow);
          }
        }

        //noinspection ConstantConditions // Vanilla usage
        player.addStat(StatList.getObjectUseStats(this));
      }
    }
  }

  @Override public int getItemEnchantability() {
    return material.getEnchantability() / 3;
  }

  @Override public boolean hitEntity(ItemStack item, EntityLivingBase target, EntityLivingBase attacker) {
    if (Config.current().meleeBowKnockback.get()) {
      int punch = Utils.getEnchLevel(item, Enchantments.PUNCH) + 3;
      double x = -MathHelper.sin(attacker.rotationYaw * Utils.PI / 180.0F) * punch * 0.5;
      double z = MathHelper.cos(attacker.rotationYaw * Utils.PI / 180.0F) * punch * 0.5;
      target.addVelocity(x, 0.2, z);

      item.damageItem(2, attacker);
      return true;
    }

    return false;
  }

  public static boolean shotBySilverBow(DamageSource source) {
    if ("arrow".equals(source.damageType)) {
      Entity arrow = source.getSourceOfDamage();
      return arrow != null && arrow.getEntityData().getBoolean(ARROW_FX);
    }
    return false;
  }

}
