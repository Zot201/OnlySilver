package zotmc.onlysilver;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import zotmc.onlysilver.api.OnlySilverRegistry;
import zotmc.onlysilver.config.Config;
import zotmc.onlysilver.data.Instrumenti;
import zotmc.onlysilver.data.ModData.OnlySilvers;
import zotmc.onlysilver.data.ModData.WeaponMod;
import zotmc.onlysilver.item.ItemOnlyArmor;
import zotmc.onlysilver.item.ItemOnlyBow;
import zotmc.onlysilver.item.ItemOnlyIngot;
import zotmc.onlysilver.util.Dynamic;
import zotmc.onlysilver.util.Dynamic.Construct;
import zotmc.onlysilver.util.Feature;
import zotmc.onlysilver.util.Utils;
import zotmc.onlysilver.util.Utils.Localization;

import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.base.Enums;
import com.google.common.base.Functions;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public enum ItemFeature implements Feature<Item> {
	// vanilla
	@Recipes(value = "ᴦ", output = 9) @Ores("ingotSilver") @Important @PlainItem(ItemOnlyIngot.class) silverIngot,
	@Recipes("σ|σ") @Ores("rodSilver") @PlainItem(Item.class) silverRod,
	
	@Recipes("σσσ| ι | ι ") @Tool(ItemPickaxe.class) @ItemId("silver_pickaxe") silverPick,
	@Recipes("σσ·|σι·| ι·") @Tool(ItemAxe.class) silverAxe,
	@Recipes("·σ·|·ι·|·ι·") @Tool(ItemSpade.class) silverShovel,
	@Recipes("·σ·|·σ·|·ι·") @Tool(ItemSword.class) silverSword,
	@Recipes("σσ·| ι·| ι·") @Tool(ItemHoe.class) silverHoe,
	@Recipes(" ɾϧ|ɪ ϧ| ɾϧ") @Tool(ItemOnlyBow.class)
	@Models({"silver_bow", "silver_bow_pulling_0", "silver_bow_pulling_1", "silver_bow_pulling_2"})
	silverBow,
	
	@Recipes("σσσ|σ σ|···") @OnlyArmor(0) @ItemId("silver_helmet") silverHelm,
	@Recipes("σ σ|σσσ|σσσ") @OnlyArmor(1) @ItemId("silver_chestplate") silverChest,
	@Recipes("σσσ|σ σ|σ σ") @OnlyArmor(2) @ItemId("silver_leggings") silverLegs,
	@Recipes("···|σ σ|σ σ") @OnlyArmor(3) silverBoots,
	
	
	// mods
	@Recipes(" σ | ισ|ι  ")
	@Depends("exnihilo") @Tool(string = "exnihilo.items.hammers.ItemHammerBase")
	silverHammer {{
		recipesEnabledFactory = Dynamic.<Boolean>refer("exnihilo.data.ModData", "ALLOW_HAMMERS");
	}},
	
	@Recipes("　σσ|σ ι|  ι")
	@Depends("BiomesOPlenty") @Tool
	silverScythe {{
		itemFactory = Dynamic.<Item>construct("biomesoplenty.common.items.ItemBOPScythe")
				.via(ToolMaterial.class, ToolMaterial.IRON)
				.viaInt(-1)
				.assemble(Instrumenti.GET_IS_REPAIRABLE_SILVER)
				.assemble(Instrumenti.GET_ITEM_ENCHANTABILITY_SILVER)
				.call(Instrumenti.SET_MAX_DAMAGE, Contents.silverToolMaterial.get().getMaxUses());
	}},
	
	
	// balkon's
	@Recipes("  ϧ| ιϧ|ι σ")
	@WM(isEnabled = "flail")
	silverFlail {{
		itemFactory = Dynamic.<Item>construct("ckathode.weaponmod.item.ItemFlail")
				.via(getItemId())
				.via(ToolMaterial.class, Contents.silverToolMaterial)
				.assemble(Instrumenti.GET_IS_REPAIRABLE_SILVER);
	}},
	
	@Recipes("  σ| ι |ι  ")
	@WM(isEnabled = "spear", meleeComp = "ckathode.weaponmod.item.MeleeCompSpear")
	silverSpear,
	
	@Recipes(" σσ| ισ|ι  ")
	@WM(isEnabled = "halberd", meleeComp = "ckathode.weaponmod.item.MeleeCompHalberd")
	silverHalberd,
	
	@Recipes({"ισ", "σ|ι"})
	@WM(isEnabled = "knife", meleeComp = "ckathode.weaponmod.item.MeleeCompKnife")
	silverKnife,
	
	@Recipes("σισ|σισ| ι ")
	@WM(isEnabled = "warhammer", meleeComp = "ckathode.weaponmod.item.MeleeCompWarhammer")
	silverWarhammer,
	
	@Recipes("϶϶σ|  ϶|  ϶")
	@WM(isEnabled = "boomerang", meleeComp = "ckathode.weaponmod.item.MeleeCompBoomerang")
	silverBoomerang,
	
	@Recipes("σσσ|σισ| ι ")
	@WM(isEnabled = "battleaxe")
	silverBattleaxe {{
		Supplier<?> meleeComp = Dynamic.construct("ckathode.weaponmod.item.MeleeCompBattleaxe")
			.via(ToolMaterial.class, Contents.silverToolMaterial)
			.<Integer>assign("ignoreArmourAmount", 1);
		
		itemFactory = new WeaponMod.ItemMeleeSupplier(getItemId(), meleeComp);
	}},
	
	@Recipes("  σ| σ |ι  ")
	@WM(isEnabled = "katana")
	silverKatana {{
		Supplier<?> meleeComp = Dynamic.construct(WeaponMod.MELEE_COMPONENT)
				.via(Dynamic.refer(WeaponMod.MELEE_COMPONENT + "$MeleeSpecs", "KATANA"))
				.via(ToolMaterial.class, Contents.silverToolMaterial);
		
		itemFactory = new WeaponMod.ItemMeleeSupplier(getItemId(), meleeComp);
	}},
	
	@WM(isEnabled = {"musketbayonet", "knife"})
	silverBayonetMusket {{
		recipeFactory = new Iterable<IRecipe>() { public Iterator<IRecipe> iterator() {
			Item musket = GameRegistry.findItem(WeaponMod.MODID, "musket");
			if (silverKnife.exists() && musket != null) {
				IRecipe r = new ShapelessRecipes(
						new ItemStack(silverBayonetMusket.get()),
						Lists.newArrayList(new ItemStack(silverKnife.get()), new ItemStack(musket))
				);
				return Iterators.singletonIterator(r);
			}
			return Iterators.emptyIterator();
		}};
		
		Supplier<Item> knife = new Supplier<Item>() { public Item get() { return silverKnife.get(); }};
		Supplier<?> meleeComp = Dynamic.construct("ckathode.weaponmod.item.MeleeCompKnife")
				.via(ToolMaterial.class, Contents.silverToolMaterial);
		
		itemFactory = Dynamic.<Item>construct("ckathode.weaponmod.item.ItemMusket")
				.via(getItemId())
				.via(WeaponMod.MELEE_COMPONENT, meleeComp)
				.via(Item.class, knife)
				.assemble(Instrumenti.GET_IS_REPAIRABLE_SILVER);
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
	
	public Item orNull() {
		return value;
	}
	
	
	public String getItemId() {
		ItemId itemId = Enums.getField(this).getAnnotation(ItemId.class);
		return itemId != null ? itemId.value() : CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name());
	}
	
	private String[] getModels() {
		Models models = Enums.getField(this).getAnnotation(Models.class);
		return models != null ? models.value() : new String[] {getItemId()};
	}
	
	public ResourceLocation getGuiWatermark() {
		String[] a = getModels();
		return new ResourceLocation(OnlySilvers.MODID, "textures/items/watermark/" + (a.length > 0 ? a[0] : getItemId()) + ".png");
	}
	
	public boolean isTool() {
		Field f = Enums.getField(this);
		return f.getAnnotation(Tool.class) != null || f.getAnnotation(WM.class) != null;
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
	
	public boolean enabled(Config config) {
		Field f = Enums.getField(this);
		
		if (config != null && !important() && config.disabledFeatures.get().contains(getItemId()))
			return false;
		
		Depends depends = f.getAnnotation(Depends.class);
		if (depends != null)
			for (String modid : depends.value())
				if (!Loader.isModLoaded(modid))
					return false;
		
		WM wm = f.getAnnotation(WM.class);
		if (wm != null) {
			if (!Loader.isModLoaded(WeaponMod.MODID))
				return false;
			
			try {
				for (String weaponType : wm.isEnabled())
					if (!WeaponMod.isEnabled.via(weaponType).get())
						return false;
				
			} catch (Throwable t) {
				OnlySilver.INSTANCE.log.catching(t);
				return false;
			}
		}
		
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
						Construct<Item> c = useString ? Dynamic.<Item>construct(tool.string())
								: Dynamic.construct(tool.value());
						
						value = c.via(ToolMaterial.class, Contents.silverToolMaterial)
								.assemble(Instrumenti.GET_IS_REPAIRABLE_SILVER)
								.get();
						
					} catch (Throwable t) {
						if (useString) OnlySilver.INSTANCE.log.catching(t);
						else Utils.propagate(t);
					}
				}
			}
			
			OnlyArmor onlyArmor = f.getAnnotation(OnlyArmor.class);
			if (onlyArmor != null) {
				checkArgument(!valid);
				valid = true;
				value = new ItemOnlyArmor(Contents.silverArmorMaterial.get(), onlyArmor.value());
			}
			
			WM wm = f.getAnnotation(WM.class);
			if (wm != null && !wm.meleeComp().isEmpty()) {
				checkArgument(!valid);
				valid = true;
				
				try {
					Supplier<?> meleeComp = Dynamic.construct(wm.meleeComp())
							.via(ToolMaterial.class, Contents.silverToolMaterial);
					
					value = new WeaponMod.ItemMeleeSupplier(getItemId(), meleeComp).get();
					
				} catch (Throwable t) {
					OnlySilver.INSTANCE.log.catching(t);
				}
			}
			
			checkArgument(valid);
			
			if (value != null) {
				value.setUnlocalizedName(name()).setCreativeTab(Contents.tabOnlySilver);
				
				String id = getItemId();
				if (GameData.getItemRegistry().getNameForObject(value) == null)
					GameRegistry.registerItem(value, id);
				
				if (f.getAnnotation(ItemId.class) != null) Contents.renameMap.put(name(), id);
				
				OnlySilver.INSTANCE.proxy.registerItemModels(value, getModels());
				
				if (isTool()) {
					BaseAttributeMap attrs = new ServersideAttributeMap();
					attrs.registerAttribute(SharedMonsterAttributes.attackDamage);
					attrs.applyAttributeModifiers(new ItemStack(value).getAttributeModifiers());
					
					float damage = (float)
							attrs.getAttributeInstance(SharedMonsterAttributes.attackDamage).getAttributeValue();
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
		public String[] value();
		public int[] output() default 1;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private @interface Ores {
		public String[] value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private @interface Important { }
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private @interface Depends {
		public String[] value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private @interface PlainItem {
		public Class<? extends Item> value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private @interface Tool {
		public Class<? extends Item> value() default Item.class;
		public String string() default "";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private @interface ItemId {
		public String value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private @interface Models {
		public String[] value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private @interface OnlyArmor {
		public int value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private @interface WM {
		public String[] isEnabled();
		public String meleeComp() default "";
	}

}
