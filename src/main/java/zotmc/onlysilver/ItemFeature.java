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

import com.google.common.base.*;
import com.google.common.collect.Lists;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import zotmc.onlysilver.api.OnlySilverRegistry;
import zotmc.onlysilver.config.Config;
import zotmc.onlysilver.data.Instrumenti;
import zotmc.onlysilver.data.ModData.OnlySilvers;
import zotmc.onlysilver.item.ItemOnlyArmor;
import zotmc.onlysilver.item.ItemOnlyAxe;
import zotmc.onlysilver.item.ItemOnlyBow;
import zotmc.onlysilver.item.ItemOnlyIngot;
import zotmc.onlysilver.util.Dynamic;
import zotmc.onlysilver.util.Dynamic.Construct;
import zotmc.onlysilver.util.Feature;
import zotmc.onlysilver.util.Utils;
import zotmc.onlysilver.util.Utils.Localization;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static net.minecraft.inventory.EntityEquipmentSlot.*;

@SuppressWarnings("Guava")
public enum ItemFeature implements Feature<Item> {
  // vanilla
  @Recipes(value = "ᴦ", output = 9) @Ores("ingotSilver") @Important @PlainItem(ItemOnlyIngot.class) silverIngot,
  @Recipes("σ|σ") @Ores("rodSilver") @PlainItem(Item.class) silverRod,
  
  @Recipes("σσσ| ι | ι ") @Tool(ItemPickaxe.class) @ItemId("silver_pickaxe") silverPick,
  @Recipes("σσ·|σι·| ι·") @Tool(ItemOnlyAxe.class) silverAxe,
  @Recipes("·σ·|·ι·|·ι·") @Tool(ItemSpade.class) silverShovel,
  @Recipes("·σ·|·σ·|·ι·") @Tool(ItemSword.class) silverSword,
  @Recipes("σσ·| ι·| ι·") @Tool(ItemHoe.class) silverHoe,
  @Recipes(" ɾϧ|ɪ ϧ| ɾϧ") @Tool(ItemOnlyBow.class) silverBow,
  
  @Recipes("σσσ|σ σ|···") @OnlyArmor(HEAD) @ItemId("silver_helmet") silverHelm,
  @Recipes("σ σ|σσσ|σσσ") @OnlyArmor(CHEST) @ItemId("silver_chestplate") silverChest,
  @Recipes("σσσ|σ σ|σ σ") @OnlyArmor(LEGS) @ItemId("silver_leggings") silverLegs,
  @Recipes("···|σ σ|σ σ") @OnlyArmor(FEET) silverBoots,
  
  
  // mods
  @Recipes(" σ | ισ|ι  ")
  @Depends("exnihilo") @Tool(string = "exnihilo.items.hammers.ItemHammerBase")
  silverHammer {{
    recipesEnabledFactory = Dynamic.refer("exnihilo.data.ModData", "ALLOW_HAMMERS");
  }};

  
  
  Supplier<Boolean> recipesEnabledFactory;
  Iterable<IRecipe> recipeFactory;
  Supplier<? extends Item> itemFactory;
  private Item value;
  
  @Override public boolean exists() {
    return value != null;
  }
  
  @Override public Item get() {
    checkState(exists());
    return value;
  }
  
  public @Nullable Item orNull() {
    return value;
  }
  
  
  public String getItemId() {
    ItemId itemId = Enums.getField(this).getAnnotation(ItemId.class);
    return itemId != null ? itemId.value() : CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name());
  }

  private String[] getModels() {
    return new String[] {getItemId()};
  }

  public ResourceLocation getGuiWatermark() {
    String[] a = getModels();
    return new ResourceLocation(OnlySilvers.MODID, "textures/items/watermark/" + (a.length > 0 ? a[0] : getItemId()) + ".png");
  }
  
  public boolean isTool() {
    Field f = Enums.getField(this);
    return f.getAnnotation(Tool.class) != null;
  }
  
  public boolean isArmor() {
    return Enums.getField(this).getAnnotation(OnlyArmor.class) != null;
  }
  
  public boolean important() {
    return Enums.getField(this).getAnnotation(Important.class) != null;
  }
  
  public Localization getLocalization() {
    return Utils.localize("item." + name() + ".name");
  }
  
  public boolean enabled(@Nullable Config config) {
    Field f = Enums.getField(this);
    
    if (config != null && !important() && config.disabledFeatures.get().contains(getItemId()))
      return false;
    
    Depends depends = f.getAnnotation(Depends.class);
    if (depends != null)
      for (String modid : depends.value())
        if (!Loader.isModLoaded(modid))
          return false;

    return true;
  }
  
  private boolean recipesEnabled() {
    if (recipesEnabledFactory != null)
      try {
        return recipesEnabledFactory.get();
        
      } catch (Throwable t) {
        OnlySilver.INSTANCE.log.catching(t);
        return false;
      }
    
    return true;
  }
  
  void initItem() {
    if (enabled(Config.current())) {
      Field f = Enums.getField(this);
      Item value = null;
      boolean valid = false;
      
      if (itemFactory != null) {
        valid = true;
        
        try {
          value = itemFactory.get();
        } catch (Throwable t) {
          OnlySilver.INSTANCE.log.catching(t);
        }
      }
      
      PlainItem plainItem = f.getAnnotation(PlainItem.class);
      if (plainItem != null) {
        checkArgument(!valid);
        valid = true;
        
        value = Dynamic.construct(plainItem.value()).get();
      }
      
      Tool tool = f.getAnnotation(Tool.class);
      if (tool != null) {
        boolean useString = !tool.string().isEmpty();
        boolean useClass = tool.value() != Item.class;
        checkArgument(!(useString && useClass));
        
        if (useString || useClass) {
          checkArgument(!valid);
          valid = true;
          
          try {
            Construct<Item> c = useString ? Dynamic.construct(tool.string())
                : Dynamic.construct(tool.value());
            
            value = c.via(ToolMaterial.class, Contents.silverToolMaterial)
                .assemble(Instrumenti.GET_IS_REPAIRABLE_SILVER)
                .get();
            
          } catch (Throwable t) {
            if (useString) OnlySilver.INSTANCE.log.catching(t);
            else throw Utils.propagate(t);
          }
        }
      }
      
      OnlyArmor onlyArmor = f.getAnnotation(OnlyArmor.class);
      if (onlyArmor != null) {
        checkArgument(!valid);
        valid = true;
        value = new ItemOnlyArmor(Contents.silverArmorMaterial.get(), onlyArmor.value());
      }

      checkArgument(valid);

      if (value != null) {
        value.setUnlocalizedName(name()).setCreativeTab(Contents.tabOnlySilver);

        String id = getItemId();
        value.setRegistryName(id);
        if (Item.REGISTRY.getObject(value.getRegistryName()) != value) {
          GameRegistry.register(value);
        }

        if (f.getAnnotation(ItemId.class) != null) Contents.renameMap.put(name(), id);
        
        OnlySilver.INSTANCE.proxy.registerItemModels(value, getModels());

        if (isTool()) {
          AbstractAttributeMap attrs = new AttributeMap();
          attrs.registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
          // TODO: Refactor for dual wielding
          attrs.applyAttributeModifiers(new ItemStack(value).getAttributeModifiers(EntityEquipmentSlot.MAINHAND));

          float damage = (float)
              attrs.getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
          damage = 6 + Math.max(0, damage - 2 - Contents.silverToolMaterial.get().getDamageVsEntity());
          OnlySilverRegistry.registerWerewolfDamage(value, Functions.constant(damage));
        }
      }
      
      this.value = value;
    }
  }
  
  void initRecipes() {
    if (exists()) {
      Field f = Enums.getField(this);
      
      Ores ores = f.getAnnotation(Ores.class);
      if (ores != null)
        for (String s : ores.value())
          OreDictionary.registerOre(s, value);
      
      if (recipesEnabled()) {
        if (recipeFactory != null)
          try {
            for (IRecipe r : recipeFactory)
              GameRegistry.addRecipe(r);
            
          } catch (Throwable t) {
            OnlySilver.INSTANCE.log.catching(t);
          }
        
        Splitter sp = Splitter.on('|').trimResults(CharMatcher.is('·')).omitEmptyStrings();
        Recipes recipes = f.getAnnotation(Recipes.class);
        if (recipes != null) {
          int outputLength = recipes.output().length;
          checkArgument(outputLength == 1 || outputLength == recipes.value().length);
          
          for (int i = 0; i < recipes.value().length; i++) {
            List<Object> args = Lists.newArrayList();
            
            for (String row : sp.split(recipes.value()[i])) {
              for (int j = 0; j < row.length(); j++) {
                char c = row.charAt(j);
                checkArgument(c == ' ' || Instrumenti.RECIPE_SYMBOLS.containsKey(c), "Unknown symbol: %s", c);
              }
              
              args.add(row);
            }
            
            for (Map.Entry<Character, Object> entry : Instrumenti.RECIPE_SYMBOLS.entrySet()) {
              args.add(entry.getKey());
              args.add(entry.getValue());
            }
            
            GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(get(), recipes.output()[outputLength == 1 ? 0 : i]), args.toArray()));
          }
        }
      }
    }
  }
  
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  private @interface Recipes {
    String[] value();
    int[] output() default 1;
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  private @interface Ores {
    String[] value();
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  private @interface Important { }
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  private @interface Depends {
    String[] value();
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  private @interface PlainItem {
    Class<? extends Item> value();
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  private @interface Tool {
    Class<? extends Item> value() default Item.class;
    String string() default "";
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  private @interface ItemId {
    String value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  private @interface OnlyArmor {
    EntityEquipmentSlot value();
  }

}
