package zotmc.onlysilver.block;

import static net.minecraft.init.Blocks.air;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import zotmc.onlysilver.entity.EntitySilverGolem;

public class BlockOnlyStorage extends BlockOnlySilver {

	public BlockOnlyStorage(Material material, String loc) {
		super(material, loc);
	}
	
	public int newMetadata(World world, int x, int y, int z) {
		return world.getBlock(x, y + 1, z) instanceof BlockPumpkin ? 1 : 0;
	}
	public void setMetadata(World world, int x, int y, int z, int newMeta) {
		world.setBlockMetadataWithNotify(x, y, z, newMeta, 4); // 0b100
	}
	
	@Override public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);

		int newMeta = newMetadata(world, x, y, z);
		if (world.getBlockMetadata(x, y, z) != newMeta)
			setMetadata(world, x, y, z, newMeta);
	}
	
	@Override public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		super.onNeighborBlockChange(world, x, y, z, block);
		
		int oldMeta = world.getBlockMetadata(x, y, z);
		int newMeta = newMetadata(world, x, y, z);
		if (newMeta != oldMeta)
			setMetadata(world, x, y, z, newMeta);
		
		if (oldMeta != 0 || newMeta != 1)
			return;
		y++;
		
		
		if (world.getBlock(x, y - 2, z) == this) {
			boolean xAxis = world.getBlock(x - 1, y - 1, z) == this
					&& world.getBlock(x + 1, y - 1, z) == this;
			boolean zAxis = world.getBlock(x, y - 1, z - 1) == this
					&& world.getBlock(x, y - 1, z + 1) == this;

			if (xAxis || zAxis)
				spawnGolem(world, x, y, z, xAxis);
		}
	}
	
	private void spawnGolem(World world, int x, int y, int z, boolean xAxis) {
		world.setBlock(x, y, z, air, 0, 2);
		world.setBlock(x, y - 1, z, air, 0, 2);
		world.setBlock(x, y - 2, z, air, 0, 2);

		if (xAxis) {
			world.setBlock(x - 1, y - 1, z, air, 0, 2);
			world.setBlock(x + 1, y - 1, z, air, 0, 2);
		}
		else {
			world.setBlock(x, y - 1, z - 1, air, 0, 2);
			world.setBlock(x, y - 1, z + 1, air, 0, 2);
		}

		EntitySilverGolem silverGolem = new EntitySilverGolem(world);
		silverGolem.setPlayerCreated(true);
		silverGolem.setLocationAndAngles(x + 0.5, y - 1.95, z + 0.5, 0, 0);
		world.spawnEntityInWorld(silverGolem);

		for (int i = 0; i < 120; i++)
			world.spawnParticle("snowballpoof",
					x + world.rand.nextDouble(),
					y - 2 + world.rand.nextDouble() * 3.9,
					z + world.rand.nextDouble(),
					0, 0, 0);

		world.notifyBlockChange(x, y, z, air);
		world.notifyBlockChange(x, y - 1, z, air);
		world.notifyBlockChange(x, y - 2, z, air);

		if (xAxis) {
			world.notifyBlockChange(x - 1, y - 1, z, air);
			world.notifyBlockChange(x + 1, y - 1, z, air);
		}
		else {
			world.notifyBlockChange(x, y - 1, z - 1, air);
			world.notifyBlockChange(x, y - 1, z + 1, air);
		}
	}

}
