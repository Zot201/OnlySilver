package zotmc.onlysilver;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPED;
import static zotmc.onlysilver.Config.silverBlockPrioritize;
import static zotmc.onlysilver.Contents.silverAxe;
import static zotmc.onlysilver.Contents.silverBattleaxe;
import static zotmc.onlysilver.Contents.silverBayonetMusket;
import static zotmc.onlysilver.Contents.silverBlock;
import static zotmc.onlysilver.Contents.silverBoomerang;
import static zotmc.onlysilver.Contents.silverBoots;
import static zotmc.onlysilver.Contents.silverBow;
import static zotmc.onlysilver.Contents.silverChest;
import static zotmc.onlysilver.Contents.silverFlail;
import static zotmc.onlysilver.Contents.silverHalberd;
import static zotmc.onlysilver.Contents.silverHelm;
import static zotmc.onlysilver.Contents.silverHoe;
import static zotmc.onlysilver.Contents.silverIngot;
import static zotmc.onlysilver.Contents.silverKatana;
import static zotmc.onlysilver.Contents.silverKnife;
import static zotmc.onlysilver.Contents.silverLegs;
import static zotmc.onlysilver.Contents.silverOre;
import static zotmc.onlysilver.Contents.silverPick;
import static zotmc.onlysilver.Contents.silverRod;
import static zotmc.onlysilver.Contents.silverShovel;
import static zotmc.onlysilver.Contents.silverSpear;
import static zotmc.onlysilver.Contents.silverSword;
import static zotmc.onlysilver.Contents.silverWarhammer;
import static zotmc.onlysilver.OnlySilver.MODID;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public class Recipes {
	
	public static final String
	SILVER_ORE = "oreSilver",
	SILVER_INGOT = "ingotSilver",
	STICK = "stickWood",
	PLANK = "plankWood";
	
	static void init() {
		OreDictionary.registerOre(SILVER_ORE, new ItemStack(silverOre.get()));
		OreDictionary.registerOre(SILVER_INGOT, new ItemStack(silverIngot.get()));

		
		if (silverBlockPrioritize.get()) {
			IRecipe r = new ShapedOreRecipe(silverBlock.get(),
					"XXX", "XXX", "XXX", 'X', SILVER_INGOT) { };
					
			RecipeSorter.register(MODID + ":silverblock", r.getClass(), SHAPED, "before:minecraft:shaped");
			Raws.<IRecipe>castRaw(CraftingManager.getInstance().getRecipeList()).add(0, r);
		}
		else
			GameRegistry.addRecipe(new ShapedOreRecipe(silverBlock.get(),
					"XXX", "XXX", "XXX", 'X', SILVER_INGOT));
		

		GameRegistry.addShapelessRecipe(new ItemStack(silverIngot.get(), 9),
				silverBlock.get());

		GameRegistry.addRecipe(new ShapedOreRecipe(silverRod.get(),
				"X", "X", 'X', SILVER_INGOT));

		GameRegistry.addRecipe(new ShapedOreRecipe(silverPick.get(),
				"XXX", " Y ", " Y ", 'X', SILVER_INGOT, 'Y', STICK));

		GameRegistry.addRecipe(new ShapedOreRecipe(silverAxe.get(),
				"XX ", "XY ", " Y ", 'X', SILVER_INGOT, 'Y', STICK));

		GameRegistry.addRecipe(new ShapedOreRecipe(silverShovel.get(),
				"X", "Y", "Y", 'X', SILVER_INGOT, 'Y', STICK));

		GameRegistry.addRecipe(new ShapedOreRecipe(silverSword.get(),
				"X", "X", "Y", 'X', SILVER_INGOT, 'Y', STICK));

		GameRegistry.addRecipe(new ShapedOreRecipe(silverHoe.get(),
				"XX ", " Y ", " Y ", 'X', SILVER_INGOT, 'Y', STICK));

		GameRegistry.addRecipe(new ItemStack(silverBow.get(), 1),
				" XY", "Z Y", " XY", 'X', silverRod.get(), 'Y', Items.string, 'Z', Items.iron_ingot);

		GameRegistry.addRecipe(new ShapedOreRecipe(silverHelm.get(),
				"XXX", "X X", 'X', SILVER_INGOT));

		GameRegistry.addRecipe(new ShapedOreRecipe(silverChest.get(),
				"X X", "XXX", "XXX", 'X', SILVER_INGOT));

		GameRegistry.addRecipe(new ShapedOreRecipe(silverLegs.get(),
				"XXX", "X X", "X X", 'X', SILVER_INGOT));

		GameRegistry.addRecipe(new ShapedOreRecipe(silverBoots.get(),
				"X X", "X X", 'X', SILVER_INGOT));

		GameRegistry.addSmelting(silverOre.get(),
				new ItemStack(silverIngot.get(), 1, 0), 0.8F);
		
		
		
		
		
		if (silverSpear.exists())
			GameRegistry.addRecipe(new ShapedOreRecipe(silverSpear.get(),
					"  I", " / ", "/  ", 'I', SILVER_INGOT, '/', STICK));
		
		if (silverHalberd.exists())
			GameRegistry.addRecipe(new ShapedOreRecipe(silverHalberd.get(),
					" II", " /I", "/  ", 'I', SILVER_INGOT, '/', STICK));
		
		if (silverKnife.exists()) {
			GameRegistry.addRecipe(new ShapedOreRecipe(silverKnife.get(),
					"I/", 'I', SILVER_INGOT, '/', STICK));

			GameRegistry.addRecipe(new ShapedOreRecipe(silverKnife.get(),
					"I", "/", 'I', SILVER_INGOT, '/', STICK));
			
			
			Content<Item> musket = Content.of("weaponmod", "musket");
			
			if (musket.exists() && silverBayonetMusket.exists())
				GameRegistry.addRecipe(new ShapelessOreRecipe(silverBayonetMusket.get(),
						silverKnife.get(), musket.get()));
			
		}
		
		if (silverBattleaxe.exists())
			GameRegistry.addRecipe(new ShapedOreRecipe(silverBattleaxe.get(),
					"III", "I/I", " / ", 'I', SILVER_INGOT, '/', STICK));
		
		if (silverWarhammer.exists())
			GameRegistry.addRecipe(new ShapedOreRecipe(silverWarhammer.get(),
					"I/I", "I/I", " / ", 'I', SILVER_INGOT, '/', STICK));
		
		if (silverFlail.exists())
			GameRegistry.addRecipe(new ShapedOreRecipe(silverFlail.get(),
					"  s", " /s", "/ I", 'I', SILVER_INGOT, '/', STICK, 's', Items.string));
		
		if (silverBoomerang.exists())
			GameRegistry.addRecipe(new ShapedOreRecipe(silverBoomerang.get(),
					"XXI", "  X", "  X", 'I', SILVER_INGOT, 'X', PLANK));
		
		if (silverKatana.exists())
			GameRegistry.addRecipe(new ShapedOreRecipe(silverKatana.get(),
					"  I", " I ", "/  ", 'I', SILVER_INGOT, '/', STICK));
		
		
	}
	
}
