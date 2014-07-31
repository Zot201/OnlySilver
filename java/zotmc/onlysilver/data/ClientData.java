package zotmc.onlysilver.data;

import static cpw.mods.fml.relauncher.Side.CLIENT;

import java.lang.reflect.Field;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import zotmc.onlysilver.util.MethodInfo;
import zotmc.onlysilver.util.Obfs;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(CLIENT)
public class ClientData {
	
	public static final MethodInfo
	REGISTER_ICONS = Obfs.findMethod(Item.class, "registerIcons", "func_94581_a")
		.asMethodInfo(IIconRegister.class),
	REGISTER_ICON = Obfs.findMethod(IIconRegister.class, "registerIcon", "func_94245_a")
		.asMethodInfo(String.class),
	GET_ICON_STRING = Obfs.findMethod(Item.class, "getIconString", "func_111208_A")
		.asMethodInfo();
	
	public static final Field
	ITEM_ICON = Obfs.findField(Item.class, "itemIcon", "field_77791_bV");
	
	
}
