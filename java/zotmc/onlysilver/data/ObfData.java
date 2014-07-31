package zotmc.onlysilver.data;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import zotmc.onlysilver.util.MethodInfo;
import zotmc.onlysilver.util.Obfs;

import com.google.common.reflect.Invokable;

public class ObfData {
	
	public static final MethodInfo
	GET_IS_REPAIRABLE = Obfs.findMethod(Item.class, "getIsRepairable", "func_82789_a")
		.asMethodInfo(ItemStack.class, ItemStack.class),
	GET_ITEM_ENCHANTABILITY = Obfs.findMethod(Item.class, "getItemEnchantability", "func_77619_b")
		.asMethodInfo();
	
	public static final Invokable<Block, Void>
	DROP_BLOCK_AS_ITEM = Obfs.findMethod(Block.class, "dropBlockAsItem", "func_149642_a")
		.asInvokable(World.class, int.class, int.class, int.class, ItemStack.class)
		.returning(void.class);
	
	public static final Invokable<Item, Item>
	SET_MAX_DAMAGE = Obfs.findMethod(Item.class, "setMaxDamage", "func_77656_e")
		.asInvokable(int.class)
		.returning(Item.class);
	
	
	public static final Field
	INVULNERABLE = Obfs.findField(Entity.class, "invulnerable", "field_83001_bt");
	
	
}
