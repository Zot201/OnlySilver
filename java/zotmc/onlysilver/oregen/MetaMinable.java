package zotmc.onlysilver.oregen;

import static net.minecraft.util.MathHelper.cos;
import static net.minecraft.util.MathHelper.sin;
import static zotmc.onlysilver.oregen.BlockDef.W;
import static zotmc.onlysilver.util.Utils.PI;
import static zotmc.onlysilver.util.Utils.floor;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class MetaMinable extends WorldGenerator {
	
	private final BlockData ore, base;
	private final int size;
	
	public MetaMinable(BlockData ore, int size, BlockData base) {
		this.ore = ore;
		this.size = size;
		this.base = base;
	}

	@Override public boolean generate(World world, Random rand, int x, int y, int z) {
		float pole = rand.nextFloat() * PI;
		double pX = x + 8 + sin(pole) * size / 8.0F;
		double qX = x + 8 - sin(pole) * size / 8.0F;
		double pZ = z + 8 + cos(pole) * size / 8.0F;
		double qZ = z + 8 - cos(pole) * size / 8.0F;
		double pY = y + rand.nextInt(3) - 2;
		double qY = y + rand.nextInt(3) - 2;
		
		for (int step = 0; step <= size; step++) {
			double rX = pX + (qX - pX) * step / size;
			double rY = pY + (qY - pY) * step / size;
			double rZ = pZ + (qZ - pZ) * step / size;
			double f = rand.nextDouble() * size / 16.0;
			double h = (sin(step * PI / size) + 1.0F) * f + 1;
			double v = (sin(step * PI / size) + 1.0F) * f + 1; //same value?
			int i0 = floor(rX - h / 2);
			int j0 = floor(rY - v / 2);
			int k0 = floor(rZ - h / 2);
			int i1 = floor(rX + h / 2);
			int j1 = floor(rY + v / 2);
			int k1 = floor(rZ + h / 2);

			for (int i = i0; i <= i1; i++) {
				double dX = (i + 0.5 - rX) / (h / 2);

				if (dX * dX < 1)
					for (int j = j0; j <= j1; j++) {
						double dY = (j + 0.5D - rY) / (v / 2);

						if (dX * dX + dY * dY < 1)
							for (int k = k0; k <= k1; k++) {
								double dZ = (k + 0.5D - rZ) / (h / 2);

								if (dX * dX + dY * dY + dZ * dZ < 1
										&& isReplaceableOreGen(world, i, j, k))
									world.setBlock(i, j, k, ore.block, ore.meta, 2);
							}
					}
			}
		}

		return true;
	}
	
	protected boolean isReplaceableOreGen(World world, int x, int y, int z) {
		if (base.meta == W)
			return world.getBlock(x, y, z).isReplaceableOreGen(world, x, y, z, base.block);
		
		return world.getBlock(x, y, z) == base.block
				&& world.getBlockMetadata(x, y, z) == base.meta;
	}
	
}
