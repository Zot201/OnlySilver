package zotmc.onlysilver.data;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import java.util.Map;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import zotmc.onlysilver.CommonHooks;
import zotmc.onlysilver.util.Klas.KlastWriter;
import zotmc.onlysilver.util.Consumer;
import zotmc.onlysilver.util.Utils;
import zotmc.onlysilver.util.init.MethodInfo;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.Invokable;

public class Instrumenti {
	
	private static final MethodInfo
	GET_IS_REPAIRABLE = Utils.findMethod(Item.class, "getIsRepairable", "func_82789_a")
		.asMethodInfo(ItemStack.class, ItemStack.class),
	GET_ITEM_ENCHANTABILITY = Utils.findMethod(Item.class, "getItemEnchantability", "func_77619_b")
		.asMethodInfo();
	
	public static final Invokable<Item, Item>
	SET_MAX_DAMAGE = Utils.findMethod(Item.class, "setMaxDamage", "func_77656_e")
		.asInvokable(int.class)
		.returning(Item.class);
	
	
	public static final Consumer<KlastWriter<?>>
	GET_IS_REPAIRABLE_SILVER = new Consumer<KlastWriter<?>>() {
		@Override public void accept(KlastWriter<?> cw) {
			GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, GET_IS_REPAIRABLE, null, null, cw);
			mg.loadArg(1);
			mg.invokeStatic(Type.getType(CommonHooks.class), MethodInfo.of("isSilverIngot", "(Lnet/minecraft/item/ItemStack;)Z"));
			mg.returnValue();
			mg.endMethod();
		}
	},
	GET_ITEM_ENCHANTABILITY_SILVER = new Consumer<KlastWriter<?>>() {
		@Override public void accept(KlastWriter<?> cw) {
			GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, GET_ITEM_ENCHANTABILITY, null, null, cw);
			mg.invokeStatic(Type.getType(CommonHooks.class), MethodInfo.of("getSilverToolEnchantibility", "()I"));
			mg.returnValue();
			mg.endMethod();
		}
	};
	
	public static final Map<Character, Object> RECIPE_SYMBOLS = ImmutableMap.<Character, Object>builder()
			.put('ι', "stickWood")
			.put('σ', "ingotSilver")
			.put('ϧ', Items.string)
			.put('ɾ', "rodSilver")
			.put('ɪ', "ingotIron")
			.put('϶', "plankWood")
			.put('ᴦ', "blockSilver")
			.build();
	
}
