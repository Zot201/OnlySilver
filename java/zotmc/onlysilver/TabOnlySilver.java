package zotmc.onlysilver;

import static net.minecraft.init.Items.enchanted_book;
import static zotmc.onlysilver.Contents.everlasting;
import static zotmc.onlysilver.Contents.incantation;
import static zotmc.onlysilver.Contents.silverIngot;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TabOnlySilver extends CreativeTabs {

	public TabOnlySilver() {
		super("tab" + OnlySilver.NAME);
	}

	@Override public Item getTabIconItem() {
		return silverIngot.get();
	}
	
	
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("rawtypes")
	@Override public void displayAllReleventItems(List list) {
		super.displayAllReleventItems(list);
		
		if (everlasting.exists())
			enchanted_book.func_92113_a(everlasting.get(), list);
		
		if (incantation.exists())
			enchanted_book.func_92113_a(incantation.get(), list);
		
	}

}
