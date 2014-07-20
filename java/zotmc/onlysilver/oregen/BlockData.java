package zotmc.onlysilver.oregen;

import static zotmc.onlysilver.oregen.BlockDef.at;
import net.minecraft.block.Block;

import com.google.common.base.Objects;

import cpw.mods.fml.common.registry.GameData;

final class BlockData {
	
	public final Block block;
	public final int meta;
	
	public BlockData(Block block, int meta) {
		this.block = block;
		this.meta = meta;
	}
	
	@Override public int hashCode() {
		return Objects.hashCode(block, meta);
	}
	
	@Override public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof BlockData) {
			BlockData o = (BlockData) obj;
			return o.block == block && o.meta == meta;
		}
		return false;
	}
	
	@Override public String toString() {
		return GameData.getBlockRegistry().getNameForObject(block) + at(meta);
	}
	
}