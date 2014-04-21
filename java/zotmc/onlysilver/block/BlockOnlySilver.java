package zotmc.onlysilver.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import zotmc.onlysilver.OnlySilver;

public class BlockOnlySilver extends Block {
	
	public BlockOnlySilver(Material material, String loc) {
		super(material);
		setBlockTextureName("onlysilver:" + loc).setCreativeTab(OnlySilver.TAB_ONLY_SILVER);
	}

}