package zotmc.onlysilver.item;

import java.util.List;
import java.util.Map.Entry;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.ShapedOreRecipe;
import zotmc.onlysilver.Contents;
import zotmc.onlysilver.Recipes;
import zotmc.onlysilver.util.Feature;
import zotmc.onlysilver.util.Utils;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;

class RecipeUtils {
	
	private static final Splitter
	SP1 = Splitter.on('|').trimResults(CharMatcher.is('·')).omitEmptyStrings(),
	SP2 = Splitter.on("||");
	
	private static final ImmutableMap<Character, Feature<?>> SYMBOLS = ImmutableMap
			.<Character, Feature<?>>builder()
			.put('ι', Utils.featureOf("stickWood"))
			.put('σ', Utils.featureOf(Recipes.INGOT_SILVER))
			.put('ϧ', Utils.featureOf(Items.string))
			.put('ɾ', Contents.silverRod)
			.put('ɪ', Utils.featureOf(Items.iron_ingot))
			.put('϶', Utils.featureOf(Blocks.planks))
			.put('ᴦ', Utils.featureOf(Recipes.BLOCK_SILVER))
			.build();
	
	
	
	public static Runnable addRecipe(final Supplier<Item> item, final String shapes) {
		return new AddRecipe(item, shapes);
	}
	
	private static class AddRecipe implements Runnable {
		private final Supplier<Item> item;
		private final String shapes;
		private AddRecipe(Supplier<Item> item, String shapes) {
			this.item = item;
			this.shapes = shapes;
		}

		@Override public void run() {
			for (String shape : SP2.split(shapes))
				run(shape);
		}
		
		private void run(String shape) {
			List<Object> args = Lists.<Object>newArrayList(SP1.split(shape));
			for (Entry<Character, Feature<?>> entry : SYMBOLS.entrySet())
				if (entry.getValue().exists()) {
					args.add(entry.getKey());
					args.add(entry.getValue().get());
				}
			
			GameRegistry.addRecipe(new ShapedOreRecipe(
					item.get(),
					args.toArray()
			));
		}
	}
	
	
}