package zotmc.onlysilver.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import zotmc.onlysilver.Obfuscations;
import zotmc.onlysilver.OnlySilver;
import zotmc.onlysilver.Recipes;
import zotmc.onlysilver.util.Consumer;

public class ItemOnlyIngot extends Item {

	public ItemOnlyIngot(String name) {
		setTextureName(OnlySilver.MODID + ":" + name);
		setUnlocalizedName(name);
		setCreativeTab(OnlySilver.instance.tabOnlySilver);
	}
	
}
