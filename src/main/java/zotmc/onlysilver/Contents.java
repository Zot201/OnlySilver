package zotmc.onlysilver;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
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
import zotmc.onlysilver.util.Feature;
import zotmc.onlysilver.util.Fields;
import zotmc.onlysilver.util.FluentMultiset;
import zotmc.onlysilver.util.Reserve;
import zotmc.onlysilver.util.Utils;

import com.google.common.base.Predicate;

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
			return Item.getItemFromBlock(silverBlock.get());
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
		GameRegistry.registerBlock(b, "silver_ore");
		silverOre.set(b);
		OnlySilver.INSTANCE.proxy.registerItemModels(b, "silver_ore");
		OreDictionary.registerOre("oreSilver", b);
		
		// silver block
		b = new BlockOnlyCompressed(MapColor.quartzColor).setUnlocalizedName("silverBlock").setCreativeTab(tabOnlySilver);
		Config.current().silverBlockStats.get().setStatTo(b, null);
		GameRegistry.registerBlock(b, "silver_block");
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
		int id = Config.current().silverAuraId.get();
		if (id != -1) {
			EnchSilverAura ench = new EnchSilverAura(id, new ResourceLocation(OnlySilvers.MODID, "silver_aura"));
			ench.setName(OnlySilvers.MODID + ".silverAura");
			Enchantment.addToBookList(ench);
			silverAura.set(ench);
			CommonHooks.silverAuraExists = true;
		}
		
		// incantation
		id = Config.current().incantationId.get();
		if (id != -1) {
			Enchantment ench = new EnchIncantation(id, new ResourceLocation(OnlySilvers.MODID, "incantation"))
				.subscribeEvent()
				.setName(OnlySilvers.MODID + ".incantation");
			Enchantment.addToBookList(ench);
			incantation.set(ench);
		}
		
		// silver golem
		EntityRegistry.registerModEntity(EntitySilverGolem.class, "silverGolem", 0, OnlySilver.INSTANCE, 80, 3, true);
		Utils.EntityLists.stringToClassMapping()
			.put("onlysilver.onlysilver.silverGolem", EntitySilverGolem.class); // re-map a mistaken previous name
		OnlySilver.INSTANCE.proxy.registerEntityRenderer(EntitySilverGolem.class);
		
		// achievement
		if (ItemFeature.silverBow.exists()) {
			Achievement achievement = new Achievement("silverBowAch", "silverBowAch", 1, 7,
					ItemFeature.silverBow.get(), AchievementList.acquireIron).registerAchievement();
			
			injectFinal("buildSilverBow", buildSilverBow.set(achievement).toOptional());
		}
		
		
		
		// loots
		addLootItem(ChestGenHooks.VILLAGE_BLACKSMITH, ItemFeature.silverHelm, 1, 1, 2);
		addLootItem(ChestGenHooks.VILLAGE_BLACKSMITH, ItemFeature.silverChest, 1, 1, 2);
		addLootItem(ChestGenHooks.VILLAGE_BLACKSMITH, ItemFeature.silverLegs, 1, 1, 2);
		addLootItem(ChestGenHooks.VILLAGE_BLACKSMITH, ItemFeature.silverBoots, 1, 1, 2);
		addLootItem(ChestGenHooks.PYRAMID_DESERT_CHEST, ItemFeature.silverIngot, 4, 6, 4);
		addLootItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, ItemFeature.silverIngot, 4, 6, 4);
		addLootItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, ItemFeature.silverBoots, 1, 1, 2);
		addLootItem(ChestGenHooks.DUNGEON_CHEST, ItemFeature.silverIngot, 3, 5, 1);
		
		// silver
		OnlySilverRegistry.registerSilverPredicate(new Predicate<ItemStack>() { public boolean apply(ItemStack input) {
			return input.getItem().getIsRepairable(input, new ItemStack(ItemFeature.silverIngot.get()));
		}});
		
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
				return !(entity instanceof EntityLivingBase) ? null : ((EntityLivingBase) entity).getHeldItem();
			}
			
			@Override public void updateItem(DamageSource damage, ItemStack item) {
				Entity entity = damage.getEntity();
				if (!(entity instanceof EntityPlayer))
					throw new UnsupportedOperationException("Unable to handle item damaging for mob with generic handler");
				entity.setCurrentItemOrArmor(0, item);
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
		injectFinal("applySilverAura", new BiConsumer<ItemStack, Runnable>() { public void accept(ItemStack t, Runnable u) {
			try {
				CommonHooks.onStoppedUsing(t);
				u.run();
			} finally {
				CommonHooks.arrowLooseContext.set(null);
			}
		}});
	}
	
	private static void addLootItem(String category, Feature<Item> i, int min, int max, int weight) {
		if (i.exists()) ChestGenHooks.addItem(category, new WeightedRandomChestContent(i.get(), 0, min, max, weight));
	}
	
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
