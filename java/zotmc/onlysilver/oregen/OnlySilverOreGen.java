package zotmc.onlysilver.oregen;

import java.util.Random;

import zotmc.onlysilver.config.Config;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public enum OnlySilverOreGen implements IWorldGenerator {
	INSTANCE;
	
	@Override public void generate(Random rand, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		
		for (OreGeneration oreGen : Config.current().oreGenProfile.get().getOreGen(world.provider.dimensionId))
			oreGen.generate(rand, chunkX, chunkZ, world);
	}
	
	public static void validateProfile() {
		Config.current().oreGenProfile.get().validateProfile();
	}

}
