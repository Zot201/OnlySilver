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

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockPattern.PatternHelper;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import zotmc.onlysilver.config.Config;
import zotmc.onlysilver.data.ReflData;
import zotmc.onlysilver.entity.EntitySilverGolem;
import zotmc.onlysilver.item.ItemOnlyBow;
import zotmc.onlysilver.loading.Patcher.*;
import zotmc.onlysilver.loading.Patcher.Hook.Strategy;
import zotmc.onlysilver.util.Feature;
import zotmc.onlysilver.util.Fields;
import zotmc.onlysilver.util.Utils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("WeakerAccess")
public class CommonHooks {
  
  // item features (non-coremod callbacks)
  
  private static final List<ItemStack> ingotSilver = OreDictionary.getOres("ingotSilver");
  
  public static int getSilverToolEnchantability() {
    return Contents.silverToolMaterial.get().getEnchantability();
  }
  
  public static boolean isSilverIngot(ItemStack item) {
    for (ItemStack ore : ingotSilver)
      if (OreDictionary.itemMatches(ore, item, false))
        return true;
    return false;
  }
  
  
  
  // silver golem
  
  @Hook @Srg("func_176390_d") @ReturnBoolean(condition = true, value = true) @Static(BlockPumpkin.class)
  public static boolean canDispenserPlace(World world, BlockPos pos) {
    return Config.current().silverGolemAssembly.get() && SilverBlockPredicate.golemVacancyPattern.match(world, pos) != null;
  }
  
  @Hook @Srg("func_180673_e") @Static(BlockPumpkin.class)
  public static void trySpawnGolem(World world, BlockPos pos) {
    if (Config.current().silverGolemAssembly.get()) {
      PatternHelper match = SilverBlockPredicate.golemPattern.match(world, pos);
      
      if (match != null) {
        for (int j = 0; j < SilverBlockPredicate.golemPattern.getThumbLength(); j++)
          world.setBlockState(match.translateOffset(0, j, 0).getPos(), Blocks.AIR.getDefaultState(), 2);
        
        BlockPos golemPos = match.translateOffset(0, 1, 0).getPos();
        EntitySilverGolem golem = new EntitySilverGolem(world);
        golem.setPlayerCreated(true);
        golem.setLocationAndAngles(golemPos.getX() + 0.5, golemPos.getY() + 0.5, golemPos.getZ() + 0.5, 0, 0);
        OnlySilver.INSTANCE.proxy.spawnEntityInWorld(golem); // prevent a CME with client thread in single player
        
        for (int i = 0; i < 120; i++)
          world.spawnParticle(EnumParticleTypes.SNOWBALL,
              golemPos.getX() + world.rand.nextDouble(),
              golemPos.getY() + world.rand.nextDouble() * 3.9,
              golemPos.getZ() + world.rand.nextDouble(),
              0, 0, 0);
        
        for (int j = 0; j < SilverBlockPredicate.golemPattern.getThumbLength(); j++)
          world.notifyNeighborsRespectDebug(match.translateOffset(0, j, 0).getPos(), Blocks.AIR);
      }
    }
  }
  
  private enum SilverBlockPredicate implements Predicate<BlockWorldState> {
    INSTANCE;
    private static final List<ItemStack> blockSilver = OreDictionary.getOres("blockSilver");
    
    @Override public boolean apply(@Nullable BlockWorldState input) {
      if (input != null) {
        IBlockState block = input.getBlockState();

        //noinspection ConstantConditions
        if (block != null) {
          try {
            //noinspection ConstantConditions
            ItemStack item = block.getBlock().getPickBlock(
                block, null, Fields.get(input, ReflData.WORLD), input.getPos(), null);

            for (ItemStack ore : blockSilver)
              if (OreDictionary.itemMatches(ore, item, false))
                return true;
          }
          catch (Exception e) {
            OnlySilver.INSTANCE.log.catching(e);
          }
        }
      }
      return false;
    }

    static final BlockPattern golemVacancyPattern = FactoryBlockPattern.start()
        .aisle(" ", "#")
        .where('#', INSTANCE)
        .build();
    
    @SuppressWarnings("ConstantConditions")
    static final BlockPattern golemPattern = FactoryBlockPattern.start()
        .aisle("^", "#")
        .where('^', BlockWorldState.hasState(Fields.<Predicate<IBlockState>>get(null, ReflData.IS_PUMPKIN)))
        .where('#', INSTANCE)
        .build();
  }
  
  
  
  // enchantments

  static boolean silverAuraExists = false;
  public static final ThreadLocal<Boolean> enchantingContext = Utils.newThreadLocal(false); // false for enchanting table type usage
  public static final ThreadLocal<ItemStack> modifierContext = new ThreadLocal<>();
  public static final ThreadLocal<Double> arrowLooseContext = new ThreadLocal<>();
  
  @Hook @Srg("func_77557_a") @ReturnBoolean(condition = true, value = true)
  public static boolean canEnchantItem(EnumEnchantmentType type, Item i) {
    return type == Contents.TOOL && (i instanceof ItemSword || i instanceof ItemTool) ||
        type == Contents.BREAKABLE && i.isDamageable();
  }
  
  @Hook(Strategy.AGENT) @Srg("func_77519_f") @Static(EnchantmentHelper.class)
  public static int getLootingModifier(int i, EntityLivingBase living) {
    Feature<Enchantment> incantation = Contents.incantation;
    return !incantation.exists() ? i :
        i + (1 + EnchantmentHelper.getMaxEnchantmentLevel(incantation.get(), living)) / 2;
  }
  
  @Hook(Strategy.AGENT) @Srg("func_77509_b") @Static(EnchantmentHelper.class)
  public static int getEfficiencyModifier(int i, EntityLivingBase living) {
    // Consider active item only
    return !silverAuraExists ? i : i + Contents.silverAura.get().getAuraEfficiency(living.getActiveItemStack());
  }
  
  public static boolean getSilverAuraDamageNegation(ItemStack item, Random rand) {
    return silverAuraExists && Contents.silverAura.get().negateDamage(item, rand);
  }
  
  public static int getSilverAuraHarvestLevel(int originalValue, EntityPlayer player) {
    if (originalValue >= 0 && silverAuraExists) {
      ItemStack item = player.getHeldItemMainhand(); // See PlayerInteractionManager#tryHarvestBlock
      int lvl = Utils.getEnchLevel(item, Contents.silverAura.get());
      if (lvl > 0) return originalValue + lvl;
    }
    return originalValue;
  }
  
  public static void onStoppedUsing(ItemStack item) {
    if (silverAuraExists) arrowLooseContext.set(Contents.silverAura.get().getAuraArrowDamage(item));
  }
  
  public static void onMobStoppedUsing(IRangedAttackMob mob) {
    // Use active item only
    ItemStack item = ((EntityLivingBase) mob).getActiveItemStack();
    if (item != null) onStoppedUsing(item);
  }
  
  @Hook @Srg("func_70097_a") @ReturnBoolean(condition = true, value = false)
  public static boolean attackEntityFrom(EntityItem entityItem, DamageSource damage, float amount) {
    return damage != DamageSource.outOfWorld && Utils.hasEnch(entityItem, Contents.silverAura);
  }

  @Hook(Strategy.RETURN) @Srg("func_180483_b")
  public static void setEnchantmentBasedOnDifficulty(EntityLiving living, DifficultyInstance difficulty) {
    if (silverAuraExists) {
      Random rand = living.getRNG();
      
      if (rand.nextInt(8) == 0) {
        float f = difficulty.getClampedAdditionalDifficulty();
        
        ItemStack item = living.getHeldItemMainhand();
        if (item != null && !item.isItemEnchanted() && rand.nextFloat() < 0.25F * f) {
          EnchantmentHelper.addRandomEnchantment(rand, item, 5 + (int) (f * rand.nextInt(18)), false);
        }

        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
          if (slot.getSlotType() == Type.ARMOR) {
            item = living.getItemStackFromSlot(slot);

            if (item != null && !item.isItemEnchanted() && rand.nextFloat() < 0.5F * f) {
              EnchantmentHelper.addRandomEnchantment(rand, item, 5 + (int) (f * rand.nextInt(18)), false);
            }
          }
        }
      }
    }
  }
  
  
  
  // skeleton AI

  public static @Nullable Item getPrototype(@Nullable Item i) {
    return i != null && i == ItemFeature.silverBow.orNull() ? Items.BOW : i;
  }

  @Hook @Srg("func_82196_d") @Return(condition = true)
  public static boolean attackEntityWithRangedAttack(EntitySkeleton attacker, EntityLivingBase target, float strength) {
    // Use active item only
    ItemStack item = attacker.getActiveItemStack();
    
    if (item != null && ItemFeature.silverBow.exists() && item.getItem() == ItemFeature.silverBow.get()) {
      World world = attacker.worldObj;
      int difficulty = world.getDifficulty().getDifficultyId();
      
      EntityArrow arrow = new EntityTippedArrow(world, attacker);
      double d0 = target.posX - attacker.posX;
      double d1 = target.getEntityBoundingBox().minY + target.height / 3.0F - arrow.posY;
      double d2 = target.posZ - attacker.posZ;
      double d3 = Math.sqrt(d0 * d0 + d2 * d2);
      arrow.setThrowableHeading(d0, d1 + d3 * 0.2, d2, 1.6F, 14 - difficulty * 4);
      arrow.getEntityData().setBoolean(ItemOnlyBow.ARROW_FX, true);
      
      double damage = strength * 2 + attacker.getRNG().nextGaussian() * 0.25 + difficulty * 0.11;
      int power = Utils.getEnchLevel(item, Enchantments.POWER);
      if (power > 0) damage += power * 0.5 + 0.5;
      arrow.setDamage(damage);
      
      arrow.setKnockbackStrength(2 + Utils.getEnchLevel(item, Enchantments.PUNCH));
      
      if (Utils.getEnchLevel(item, Enchantments.FLAME) > 0 || attacker.getSkeletonType() == 1) arrow.setFire(100);
      
      attacker.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1, 1 / (attacker.getRNG().nextFloat() * 0.4F + 0.8F));
      attacker.worldObj.spawnEntityInWorld(arrow);
      
      return true;
    }
    
    return false;
  }
  
  
  
  // equips randomization
  
  private static final Map<EntityEquipmentSlot, Feature<Item>> armorList = ImmutableMap.of(
      EntityEquipmentSlot.FEET, ItemFeature.silverBoots,
      EntityEquipmentSlot.LEGS, ItemFeature.silverLegs,
      EntityEquipmentSlot.CHEST, ItemFeature.silverChest,
      EntityEquipmentSlot.HEAD, ItemFeature.silverHelm
  );
  private static final List<Feature<Item>> weaponList = ImmutableList.of(
      ItemFeature.silverSword,
      ItemFeature.silverKatana,
      ItemFeature.silverBow,
      Utils.missingFeature()
  );
  
  @Hook @Srg("func_180481_a")
  public static void setEquipmentBasedOnDifficulty(EntityLiving living, DifficultyInstance difficulty) {
    Random rand = living.getRNG();
    
    if (rand.nextFloat() < 0.15F * difficulty.getClampedAdditionalDifficulty()) {
      int tier = rand.nextInt(2);
      if (rand.nextFloat() < 0.095F) tier++;
      if (rand.nextFloat() < 0.095F) tier++;
      if (rand.nextFloat() < 0.095F) tier++;
      
      if (tier == 2) {
        float f = living.worldObj.getDifficulty() == EnumDifficulty.HARD ? 0.9F : 0.75F;

        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
          if (slot.getSlotType() == Type.ARMOR) {
            ItemStack item = living.getItemStackFromSlot(slot);

            if (item == null && (slot == EntityEquipmentSlot.HEAD || rand.nextFloat() < f)) {
              Feature<Item> i = armorList.get(slot);

              if (i != null && i.exists()) {
                living.setItemStackToSlot(slot, new ItemStack(i.get()));
              }
            }
          }
        }
      }
    }
  }
  
  @Hook(Strategy.RETURN) @Srg("func_180481_a")
  public static void setEquipmentBasedOnDifficulty(EntitySkeleton skeleton, DifficultyInstance difficulty) {
    Random rand = skeleton.getRNG();
    int n = 0;
    
    if (hasSilverArmor(skeleton)) n = 3;
    else {
      float f = skeleton.worldObj.getDifficulty() == EnumDifficulty.HARD ? 0.08F : 0.016F;
      if (rand.nextFloat() < f) n = 1;
    }
    
    for (int k = 0; k < n; k++) {
      int j = rand.nextInt(4);
      Feature<Item> i = weaponList.get(j);
      
      if (i.exists()) {
        skeleton.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(i.get()));
        skeleton.setCombatTask();
        break;
      }
    }
  }
  
  private static boolean hasSilverArmor(EntityLiving skeleton) {
    return armorList.entrySet().stream().anyMatch(e -> {
      Feature<Item> i = e.getValue();

      if (i.exists()) {
        ItemStack item = skeleton.getItemStackFromSlot(e.getKey());

        if (item != null && item.getItem() == i.get()) {
          return true;
        }
      }
      return false;
    });
  }
  
  public static void enchantSilverSword(EntitySkeleton owner) {
    ItemStack item = owner.getHeldItemMainhand(); // See EntityLiving#setEnchantmentBasedOnDifficulty
    Random rand = owner.getRNG();
    
    if (item != null && ItemFeature.silverSword.exists() && !item.isItemEnchanted()
        && item.getItem() == ItemFeature.silverSword.get() && rand.nextFloat() < 0.25F)
      EnchantmentHelper.addRandomEnchantment(rand, item, 5 + rand.nextInt(18), false);
  }
  
}
