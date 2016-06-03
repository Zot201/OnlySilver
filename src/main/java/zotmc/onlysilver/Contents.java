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

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import zotmc.onlysilver.api.BiConsumer;
import zotmc.onlysilver.api.DamageSourceHandler;
import zotmc.onlysilver.api.OnlySilverRegistry;
import zotmc.onlysilver.api.OnlySilverUtils;
import zotmc.onlysilver.block.BlockOnlyCompressed;
import zotmc.onlysilver.block.BlockOnlyOre;
import zotmc.onlysilver.config.Config;
import zotmc.onlysilver.data.ModData.MoCreatures;
import zotmc.onlysilver.data.ModData.OnlySilvers;
import zotmc.onlysilver.data.ModData.Thaumcraft;
import zotmc.onlysilver.data.ModData.Thaumcraft.Aspect;
import zotmc.onlysilver.data.ModData.WeaponMod;
import zotmc.onlysilver.ench.EnchIncantation;
import zotmc.onlysilver.ench.EnchSilverAura;
import zotmc.onlysilver.entity.EntitySilverGolem;
import zotmc.onlysilver.util.Fields;
import zotmc.onlysilver.util.FluentMultiset;
import zotmc.onlysilver.util.Reserve;
import zotmc.onlysilver.util.Utils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("WeakerAccess")
public class Contents {
  
  static final Map<String, String> renameMap = Utils.newHashMap();
  
  public static final EnumEnchantmentType
  TOOL = EnumHelper.addEnchantmentType("TOOL"),
  BREAKABLE = EnumHelper.addEnchantmentType("BREAKABLE");
  
  public static final CreativeTabs tabOnlySilver = new CreativeTabs("tabOnlySilver") {
    {
      setRelevantEnchantmentTypes(TOOL, BREAKABLE);
    }
    @Override public Item getTabIconItem() {
      return checkNotNull(Item.getItemFromBlock(silverBlock.get()));
    }
  };
  
  public static final Reserve<Block>
  silverOre = Reserve.absent(),
  silverBlock = Reserve.absent();
  
  public static final Reserve<ToolMaterial>
  silverToolMaterial = Reserve.absent();
  
  public static final Reserve<ArmorMaterial>
  silverArmorMaterial = Reserve.absent();
  
  public static final Reserve<EnchSilverAura>
  silverAura = Reserve.absent();
  
  public static final Reserve<Enchantment>
  incantation = Reserve.absent();
  
  public static final Reserve<Achievement>
  buildSilverBow = Reserve.absent();
  
  
  public static void init() {
    // silver ore
    Block b = new BlockOnlyOre().setUnlocalizedName("silverOre").setCreativeTab(tabOnlySilver);
    Config.current().silverOreStats.get().setStatTo(b, "pickaxe");
    b.setRegistryName("silver_ore");
    GameRegistry.register(b);
    silverOre.set(b);
    OnlySilver.INSTANCE.proxy.registerItemModels(b, "silver_ore");
    OreDictionary.registerOre("oreSilver", b);
    
    // silver block
    b = new BlockOnlyCompressed(MapColor.QUARTZ).setUnlocalizedName("silverBlock").setCreativeTab(tabOnlySilver);
    Config.current().silverBlockStats.get().setStatTo(b, null);
    b.setRegistryName("silver_block");
    GameRegistry.register(b);
    silverBlock.set(b);
    OnlySilver.INSTANCE.proxy.registerItemModels(b, "silver_block");
    OreDictionary.registerOre("blockSilver", b);
    GameRegistry.addRecipe(new ShapedOreRecipe(b, "σσσ", "σσσ", "σσσ", 'σ', "ingotSilver"));
    
    // silver tool material
    silverToolMaterial.set(Config.current().silverToolStats.get().addToolMaterial("SILVER"));
    injectFinal("silverToolMaterial", silverToolMaterial.toOptional());
    
    // silver armor material
    silverArmorMaterial.set(
        Config.current().silverArmorStats.get().addArmorMaterial("SILVER", OnlySilvers.MODID + ":silver"));
    injectFinal("silverArmorMaterial", silverArmorMaterial.toOptional());
    
    // items
    for (ItemFeature f : ItemFeature.values())
      f.initItem();
    for (ItemFeature f : ItemFeature.values())
      f.initRecipes();
    
    // furnace recipes
    GameRegistry.addSmelting(silverOre.get(), new ItemStack(ItemFeature.silverIngot.get()), 0.8F);
    
    

    // silver aura
    // TODO: Allow disabling
    {
      EnchSilverAura ench = new EnchSilverAura(new ResourceLocation(OnlySilvers.MODID, "silver_aura"));
      ench.setName(OnlySilvers.MODID + ".silverAura");
      //Enchantment.addToBookList(ench); // TODO: Check effect of previous addToBookList call
      silverAura.set(ench);
      CommonHooks.silverAuraExists = true;
    }
    
    // incantation
    // TODO: Allow disabling
    {
      Enchantment ench = new EnchIncantation(new ResourceLocation(OnlySilvers.MODID, "incantation"))
          .subscribeEvent()
          .setName(OnlySilvers.MODID + ".incantation");
      //Enchantment.addToBookList(ench); // TODO: Same as above
      incantation.set(ench);
    }
    
    // silver golem
    EntityRegistry.registerModEntity(EntitySilverGolem.class, "silverGolem", 0, OnlySilver.INSTANCE, 80, 3, true);
    EntityList.NAME_TO_CLASS.put(
        "onlysilver.onlysilver.silverGolem", EntitySilverGolem.class); // re-map a mistaken previous name
    OnlySilver.INSTANCE.proxy.registerEntityRenderer(EntitySilverGolem.class);
    
    // achievement
    if (ItemFeature.silverBow.exists()) {
      Achievement achievement = new Achievement("silverBowAch", "silverBowAch", 1, 7,
          ItemFeature.silverBow.get(), AchievementList.ACQUIRE_IRON).registerStat();
      
      injectFinal("buildSilverBow", buildSilverBow.set(achievement).toOptional());
    }
    
    
    
    // loots
    // TODO: Loots
    /*addLootItem(ChestGenHooks.VILLAGE_BLACKSMITH, ItemFeature.silverHelm, 1, 1, 2);
    addLootItem(ChestGenHooks.VILLAGE_BLACKSMITH, ItemFeature.silverChest, 1, 1, 2);
    addLootItem(ChestGenHooks.VILLAGE_BLACKSMITH, ItemFeature.silverLegs, 1, 1, 2);
    addLootItem(ChestGenHooks.VILLAGE_BLACKSMITH, ItemFeature.silverBoots, 1, 1, 2);
    addLootItem(ChestGenHooks.PYRAMID_DESERT_CHEST, ItemFeature.silverIngot, 4, 6, 4);
    addLootItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, ItemFeature.silverIngot, 4, 6, 4);
    addLootItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, ItemFeature.silverBoots, 1, 1, 2);
    addLootItem(ChestGenHooks.DUNGEON_CHEST, ItemFeature.silverIngot, 3, 5, 1);*/
    
    // silver
    OnlySilverRegistry.registerSilverPredicate(
        input -> input.getItem().getIsRepairable(input, new ItemStack(ItemFeature.silverIngot.get())));
    
    // aspects
    if (Loader.isModLoaded(Thaumcraft.MODID))
      try {
        Thaumcraft.registerEntityTag(EntitySilverGolem.class,
            FluentMultiset.of(Aspect.METAL, 3).tag(Aspect.GREED, 3).tag(Aspect.EARTH, 1));
        
        Thaumcraft.registerObjectTag(new ItemStack(silverBlock.get()),
            FluentMultiset.of(Aspect.METAL, 8).tag(Aspect.GREED, 8));
        
      } catch (Throwable t) {
        OnlySilver.INSTANCE.log.error("Error while adding Thaumcraft aspects", t);
      }
    
    // werewolf
    if (Loader.isModLoaded(MoCreatures.MODID))
      try {
        OnlySilver.INSTANCE.eventBus.register(new WerewolfHandler<>(MinecraftForge.EVENT_BUS));
        
      } catch (Throwable t) {
        OnlySilver.INSTANCE.log.error("Error while adding handler for MoC werewolves", t);
      }
    
    // vanilla
    OnlySilverRegistry.registerDamageSourceHandler(new DamageSourceHandler() {
      @Override public String[] getTargetDamageTypes() {
        return new String[] {"player", "mob"};
      }
      
      @Override public ItemStack getItem(DamageSource damage) {
        Entity entity = damage.getEntity();
        return !(entity instanceof EntityLivingBase) ? null : ((EntityLivingBase) entity).getHeldItem(EnumHand.MAIN_HAND);
      }
      
      @Override public void updateItem(DamageSource damage, @Nullable ItemStack item) {
        Entity entity = damage.getEntity();
        if (entity != null) {
          damage.getEntity().setItemStackToSlot(EntityEquipmentSlot.MAINHAND, item);
        }
      }
    });
    
    // balkon's
    if (Loader.isModLoaded(WeaponMod.MODID))
      try {
        OnlySilverRegistry.registerDamageSourceHandler(new WeaponMod.ProjectileHandler<>());
        
      } catch (Throwable t) {
        OnlySilver.INSTANCE.log.error("Error while adding handler for WM projectiles", t);
      }
    
    // API
    injectFinal("applySilverAura", (BiConsumer<ItemStack, Runnable>) (t, u) -> {
      try {
        CommonHooks.onStoppedUsing(t);
        u.run();
      } finally {
        CommonHooks.arrowLooseContext.set(null);
      }
    });
  }
  
  /*private static void addLootItem(String category, Feature<Item> i, int min, int max, int weight) {
    if (i.exists()) ChestGenHooks.addItem(category, new WeightedRandomChestContent(i.get(), 0, min, max, weight));
  }*/
  
  private static void injectFinal(String name, Object value) {
    try {
      Field f = OnlySilverUtils.class.getDeclaredField(name);
      f.setAccessible(true);
      Fields.setFinal(null, f, value);
    } catch (Throwable t) {
      throw Utils.propagate(t);
    }
  }
  
}
