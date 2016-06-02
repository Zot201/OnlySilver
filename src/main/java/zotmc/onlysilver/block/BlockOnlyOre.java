package zotmc.onlysilver.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOnlyOre extends Block {

  public BlockOnlyOre() {
    super(Material.rock);
  }

  @SideOnly(Side.CLIENT)
  @Override public EnumWorldBlockLayer getBlockLayer() {
    return EnumWorldBlockLayer.CUTOUT_MIPPED;
  }

}
