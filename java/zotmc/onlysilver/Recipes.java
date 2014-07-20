package zotmc.onlysilver;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPED;
import static zotmc.onlysilver.Contents.silverBlock;
import static zotmc.onlysilver.Contents.silverIngot;
import static zotmc.onlysilver.Contents.silverOre;
import static zotmc.onlysilver.Contents.silverRod;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import zotmc.onlysilver.item.Instrumentum;
import zotmc.onlysilver.util.Dynamic;
import zotmc.onlysilver.util.Utils;
import cpw.mods.fml.common.registry.GameRegistry;

public class Recipes {
	
	public static final String
	ORE_SILVER = "oreSilver",
	BLOCK_SILVER = "blockSilver",
	INGOT_SILVER = "ingotSilver";
	
	static void init() {
		OreDictionary.registerOre(ORE_SILVER, silverOre.get());
		OreDictionary.registerOre(BLOCK_SILVER, silverBlock.get());
		OreDictionary.registerOre(INGOT_SILVER, silverIngot.get());
		
		
		IRecipe r = new ShapedOreRecipe(
				silverBlock.get(),
				"σσσ", "σσσ", "σσσ",
				'σ', INGOT_SILVER
		) { };
		RecipeSorter.register(
				OnlySilver.MODID + ":silverblock",
				r.getClass(), SHAPED, "before:minecraft:shaped"
		);
		Utils.CraftingManagers.getRecipeList().add(0, r);
		
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(silverIngot.get(), 9),
				"ᴦ",
				'ᴦ', BLOCK_SILVER
		));
		GameRegistry.addRecipe(new ShapedOreRecipe(
				silverRod.get(),
				"σ", "σ",
				'σ', INGOT_SILVER
		));
		GameRegistry.addSmelting(
				silverOre.get(),
				new ItemStack(silverIngot.get()),
				0.8F
		);
		
		
		initRecipes(Instrumentum.class);
		
	}
	
	private static void initRecipes(Class<?> clz) {
		Dynamic.<Void>invoke(clz, "initRecipes").get();
	}
	
}
