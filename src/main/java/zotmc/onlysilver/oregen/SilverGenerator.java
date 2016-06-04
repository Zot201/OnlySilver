package zotmc.onlysilver.oregen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import zotmc.onlysilver.Contents;

import java.util.Random;

public class SilverGenerator implements IWorldGenerator {

  @Override public void generate(Random rand, int chX, int chZ, World world,
      IChunkGenerator generator, IChunkProvider provider) {
    if (!world.isRemote) {
      ExtSettings ext = OreGenHandler.INSTANCE.loadExtSettings(world);

      if (ext != null && ext.silverDimensions.apply(world)) {
        BlockPos pos = new BlockPos(chX * 16, 0, chZ * 16);

        setSeed(rand, world, pos, 0xBD5866DC480CEF77L);
        WorldGenerator gen = new WorldGenMinable(Contents.silverOre.get().getDefaultState(), ext.silverSize);
        genStandardOre(world, rand, pos, ext.silverCount, gen, ext.silverMinHeight, ext.silverMaxHeight);
      }
    }
  }

  private void setSeed(Random rand, World world, BlockPos pos, long oreSeed) {
    rand.setSeed(oreSeed ^= world.getSeed());
        long xSeed = rand.nextLong() >> 2 + 1L;
        long zSeed = rand.nextLong() >> 2 + 1L;
        rand.setSeed(oreSeed ^ (xSeed * (pos.getX() >> 4) + zSeed * (pos.getZ() >> 4)));
  }

  private void genStandardOre(World world, Random rand, BlockPos pos, int count, WorldGenerator gen, int min, int max) {
    if (max < min) {
      int temp = min;
      min = max;
      max = temp;
    }
    else if (max == min) {
      if (min < 255) max++;
      else min--;
    }
    for (int i = 0; i < count; i++) {
      BlockPos p = pos.add(rand.nextInt(16), rand.nextInt(max - min) + min, rand.nextInt(16));
      gen.generate(world, rand, p);
    }
  }

}
