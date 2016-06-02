package zotmc.onlysilver.block;

import net.minecraft.block.BlockCompressed;
import net.minecraft.block.material.MapColor;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockOnlyCompressed extends BlockCompressed {

  public BlockOnlyCompressed(MapColor mapColor) {
    super(mapColor);
  }

  @Override public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
    return true;
  }

}
