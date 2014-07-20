package zotmc.onlysilver.item;

import static zotmc.onlysilver.Contents.toolSilver;
import static zotmc.onlysilver.util.BooleanSupplier.isModLoaded;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import zotmc.onlysilver.Obfuscations;
import zotmc.onlysilver.Recipes;
import zotmc.onlysilver.util.BooleanSupplier;
import zotmc.onlysilver.util.Consumer;
import zotmc.onlysilver.util.Dynamic;
import zotmc.onlysilver.util.Dynamic.Construct;
import zotmc.onlysilver.util.Dynamic.Invoke;
import zotmc.onlysilver.util.KlastWriter;
import zotmc.onlysilver.util.MethodInfo;

public class ItemUtils implements Opcodes {
	
	static class Balkon {
		
		public static final String MODID = "weaponmod";
		public static final String MELEE_COMPONENT = "ckathode.weaponmod.item.MeleeComponent";
		
		public static final BooleanSupplier isModLoaded = isModLoaded(MODID);
		public static final Invoke<Boolean> isEnabled = Dynamic
				.refer("ckathode.weaponmod.BalkonsWeaponMod", "instance")
				.refer("modConfig")
				.invoke("isEnabled");
		public static final Construct<Item> newItemMelee = Dynamic
				.construct("ckathode.weaponmod.item.ItemMelee");
		
	}
	
	
	
	public static boolean isSilverIngot(ItemStack item) {
		for (ItemStack i : OreDictionary.getOres(Recipes.INGOT_SILVER))
			if (OreDictionary.itemMatches(i, item, false))
			return true;
		return false;
	}
	
	
	
	static final Consumer<KlastWriter>
	GET_IS_REPAIRABLE = new Consumer<KlastWriter>() {
		@Override public void accept(KlastWriter cw) {
			GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC,
					Obfuscations.GET_IS_REPAIRABLE, null, null, cw
			);
			mg.loadArg(1);
			mg.invokeStatic(
					Type.getType(ItemUtils.class),
					MethodInfo.of("isSilverIngot", "(Lnet/minecraft/item/ItemStack;)Z")
			);
			mg.returnValue();
			mg.endMethod();
		}
	},
	GET_ITEM_ENCHANTABILITY = new Consumer<KlastWriter>() {
		@Override public void accept(KlastWriter cw) {
			GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC,
					Obfuscations.GET_ITEM_ENCHANTABILITY, null, null, cw
			);
			mg.push(toolSilver.get().getEnchantability());
			mg.returnValue();
			mg.endMethod();
		}
	},
	REGISTER_ICONS = new Consumer<KlastWriter>() {
		@Override public void accept(KlastWriter cw) {
			GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC,
					Obfuscations.REGISTER_ICONS, null, null, cw
			);
			mg.loadThis();
			mg.loadArgs();
			mg.loadThis();
			mg.invokeVirtual(
					cw.target.toType(),
					Obfuscations.GET_ICON_STRING
			);
			mg.invokeInterface(
					Type.getType(IIconRegister.class),
					Obfuscations.REGISTER_ICON
			);
			mg.putField(
					cw.target.toType(),
					Obfuscations.ITEM_ICON.getName(),
					Type.getType(Obfuscations.ITEM_ICON.getType())
			);
			mg.returnValue();
			mg.endMethod();
		}
	};


}
