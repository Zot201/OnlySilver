package zotmc.onlysilver.item;

import static zotmc.onlysilver.Contents.armorSilver;
import static zotmc.onlysilver.Contents.rendererPrefix;
import static zotmc.onlysilver.Contents.toolSilver;
import static zotmc.onlysilver.Obfuscations.SET_MAX_DAMAGE;
import static zotmc.onlysilver.item.Instrumentum.Syntax.ARMOR;
import static zotmc.onlysilver.item.Instrumentum.Syntax.BALKON;
import static zotmc.onlysilver.item.Instrumentum.Syntax.FLAIL;
import static zotmc.onlysilver.item.Instrumentum.Syntax.MOD;
import static zotmc.onlysilver.item.Instrumentum.Syntax.VANILLA;
import static zotmc.onlysilver.item.ItemUtils.GET_IS_REPAIRABLE;
import static zotmc.onlysilver.item.ItemUtils.GET_ITEM_ENCHANTABILITY;
import static zotmc.onlysilver.item.ItemUtils.REGISTER_ICONS;
import static zotmc.onlysilver.item.RecipeUtils.addRecipe;
import static zotmc.onlysilver.util.BooleanSupplier.alwaysTrue;
import static zotmc.onlysilver.util.BooleanSupplier.isModLoaded;

import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import zotmc.onlysilver.OnlySilver;
import zotmc.onlysilver.api.OnlySilverRegistry;
import zotmc.onlysilver.item.ItemUtils.Balkon;
import zotmc.onlysilver.util.Dynamic;
import zotmc.onlysilver.util.Feature;
import zotmc.onlysilver.util.Utils;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.registry.GameRegistry;

public enum Instrumentum implements Feature<Item> {
	//vanilla
	silverPick		("σσσ| ι | ι ", VANILLA, ItemPickaxe.class),
	silverAxe		("σσ·|σι·| ι·", VANILLA, ItemAxe.class),
	silverShovel	("·σ·|·ι·|·ι·", VANILLA, ItemSpade.class),
	silverSword		("·σ·|·σ·|·ι·", VANILLA, ItemSword.class),
	silverHoe		("σσ·| ι·| ι·", VANILLA, ItemHoe.class),
	silverBow		("　ɾϧ|ɪ ϧ| ɾϧ", VANILLA, ItemOnlyBow.class),
	silverHelm		("σσσ|σ σ|···", ARMOR, 0),
	silverChest		("σ σ|σσσ|σσσ", ARMOR, 1),
	silverLegs		("σσσ|σ σ|σ σ", ARMOR, 2),
	silverBoots		("···|σ σ|σ σ", ARMOR, 3),
	
	//mods
	silverHammer	(" σ | ισ|ι  ", MOD, "crowley.skyblock", "exnihilo.items.hammers.ItemHammerBase"),
	silverScythe	("　σσ|σ ι|  ι", MOD, "BiomesOPlenty",
			Dynamic.<Item>construct("biomesoplenty.common.items.ItemBOPScythe")
				.via(ToolMaterial.IRON)
				.viaInt(-1)
				.assemble(GET_IS_REPAIRABLE)
				.assemble(GET_ITEM_ENCHANTABILITY)
				.assemble(REGISTER_ICONS)
				.call(SET_MAX_DAMAGE, toolSilver.get().getMaxUses())
	),
	
	//balkon
	silverFlail		("  ϧ| ιϧ|ι σ", FLAIL, "flail", "ckathode.weaponmod.item.ItemFlail"),
	silverSpear		("  σ| ι |ι  ", BALKON, "spear", "ckathode.weaponmod.item.MeleeCompSpear"),
	silverHalberd	(" σσ| ισ|ι  ", BALKON, "halberd", "ckathode.weaponmod.item.MeleeCompHalberd"),
	silverKnife		(  "ισ||σ|ι"  , BALKON, "knife", "ckathode.weaponmod.item.MeleeCompKnife"),
	silverWarhammer	("σισ|σισ| ι ", BALKON, "warhammer", "ckathode.weaponmod.item.MeleeCompWarhammer"),
	silverBoomerang	("϶϶σ|  ϶|  ϶", BALKON, "boomerang", "ckathode.weaponmod.item.MeleeCompBoomerang"),
	silverBattleaxe	("σσσ|σισ| ι ", BALKON, "battleaxe",
			Dynamic.construct("ckathode.weaponmod.item.MeleeCompBattleaxe")
				.via(toolSilver)
				.assemble(GET_IS_REPAIRABLE)
				.<Integer>assign("ignoreArmourAmount", 1)
	),
	silverKatana	("  σ| σ |ι  ", BALKON, "katana",
			Dynamic.construct(Balkon.MELEE_COMPONENT)
				.via(Dynamic.refer(Balkon.MELEE_COMPONENT + "$MeleeSpecs", "KATANA"))
				.via(toolSilver)
				.assemble(GET_IS_REPAIRABLE)
	),
	silverBayonetMusket (
			new Runnable() {
				public void run() {
					Optional<Item> musket = Utils.getItem(Balkon.MODID, "musket");
					if (silverKnife.exists() && musket.isPresent())
						GameRegistry.addRecipe(new ShapelessOreRecipe(
								silverBayonetMusket.get(),
								silverKnife.get(), musket.get()
						));
				}
			},
			false,
			Balkon.isModLoaded
				.and(Balkon.isEnabled.via("musketbayonet"))
				.and(Balkon.isEnabled.via("knife")),
			Dynamic.<Item>construct("ckathode.weaponmod.item.ItemMusket")
				.via("silverBayonetMusket")
				.via(Balkon.MELEE_COMPONENT,
						Dynamic.construct("ckathode.weaponmod.item.MeleeCompKnife")
							.via(toolSilver)
				)
				.via(Item.class, silverKnife)
				.assemble(GET_IS_REPAIRABLE)
	),
	;
	
	enum Syntax {
		VANILLA,
		ARMOR,
		
		MOD,
		FLAIL,
		BALKON;
	}
	
	
	private Runnable recipes;
	private final boolean register;
	private Supplier<Boolean> enabled;
	private Supplier<? extends Item> factory;
	private Item item;
	private Syntax syntax;

	private Instrumentum(Runnable recipes, boolean register,
			Supplier<Boolean> enabled, Supplier<? extends Item> factory) {
		this.recipes = recipes;
		this.register = register;
		this.enabled = enabled;
		this.factory = factory;
	}
	
	private Instrumentum(String recipes, Syntax syntax, Class<? extends Item> clz) {
		switch (this.syntax = syntax) {
		case VANILLA:
			this.recipes = addRecipe(this, recipes);
			register = true;
			enabled = alwaysTrue();
			factory = Dynamic.construct(clz)
					.via(toolSilver)
					.assemble(GET_IS_REPAIRABLE);
			return;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	private Instrumentum(String recipes, Syntax syntax, int armorPosition) {
		switch (this.syntax = syntax) {
		case ARMOR:
			this.recipes = addRecipe(this, recipes);
			register = true;
			enabled = alwaysTrue();
			factory = Dynamic.construct(ItemOnlyArmor.class)
					.via(armorSilver)
					.viaInt(rendererPrefix)
					.viaInt(armorPosition);
			return;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	@SuppressWarnings("unchecked")
	private Instrumentum(String recipes, Syntax syntax, String species, Supplier<?> factory) {
		switch (this.syntax = syntax) {
		case MOD:
			this.recipes = addRecipe(this, recipes);
			register = true;
			enabled = isModLoaded(species);
			this.factory = (Supplier<? extends Item>) factory;
			return;
		case BALKON:
			this.recipes = addRecipe(this, recipes);
			register = false;
			enabled = Balkon.isModLoaded.and(Balkon.isEnabled.via(species));
			this.factory = Balkon.newItemMelee
					.via(toString())
					.via(Balkon.MELEE_COMPONENT, factory)
					.assemble(GET_IS_REPAIRABLE);
			return;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	private Instrumentum(String recipes, Syntax syntax, String species, String clz) {
		switch (this.syntax = syntax) {
		case MOD:
			this.recipes = addRecipe(this, recipes);
			register = true;
			enabled = isModLoaded(species);
			factory = Dynamic.<Item>construct(clz)
					.via(toolSilver)
					.assemble(GET_IS_REPAIRABLE);
			return;
		case FLAIL:
			this.recipes = addRecipe(this, recipes);
			register = false;
			enabled = Balkon.isModLoaded.and(Balkon.isEnabled.via(species));
			factory = Dynamic.<Item>construct(clz)
					.via(toString())
					.via(toolSilver)
					.assemble(GET_IS_REPAIRABLE);
			return;
		case BALKON:
			this.recipes = addRecipe(this, recipes);
			register = false;
			enabled = Balkon.isModLoaded.and(Balkon.isEnabled.via(species));
			factory = Balkon.newItemMelee
					.via(toString())
					.via(Balkon.MELEE_COMPONENT,
							Dynamic.construct(clz).via(toolSilver))
					.assemble(GET_IS_REPAIRABLE);
			return;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	
	public boolean isTool() {
		return syntax != ARMOR;
	}
	
	@Override public boolean exists() {
		return item != null;
	}
	
	@Override public Item get() {
		if (!exists())
			throw new IllegalStateException();
		return item;
	}
	
	
	private static final Set<Item> instruments = Sets.newIdentityHashSet();
	
	private static void init() {
		for (Instrumentum inst : values())
			try {
				if (inst.enabled.get()) {
					inst.item = inst.factory.get()
						.setTextureName(OnlySilver.MODID + ":" + inst.toString())
						.setUnlocalizedName(inst.toString())
						.setCreativeTab(OnlySilver.instance.tabOnlySilver);
					
					if (inst.register)
						GameRegistry.registerItem(inst.item, inst.toString());
					
					instruments.add(inst.item);
				}
				
			} catch (Throwable e) {
				OnlySilver.instance.log.catching(e);
				inst.item = null;
			} finally {
				inst.enabled = null;
				inst.factory = null;
			}
		

		OnlySilverRegistry.registerSilverItems(new Predicate<ItemStack>() {
			@Override public boolean apply(ItemStack input) {
				return input != null && instruments.contains(input.getItem());
			}
		});
		
	}
	
	private static void initRecipes() {
		for (Instrumentum inst : values())
			try {
				if (inst.exists())
					inst.recipes.run();
				
			} catch (Throwable e) {
				OnlySilver.instance.log.catching(e);
			} finally {
				inst.recipes = null;
			}
		
	}
	
}
