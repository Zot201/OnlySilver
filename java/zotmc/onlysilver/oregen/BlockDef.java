package zotmc.onlysilver.oregen;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;
import zotmc.onlysilver.util.Feature;
import zotmc.onlysilver.util.Utils;

import com.google.common.base.Optional;

import cpw.mods.fml.common.Loader;

final class BlockDef implements Feature<BlockData> {
	
	static final int W = OreDictionary.WILDCARD_VALUE;
	private static final Pattern PATTERN = Pattern.compile(
			"^\"(.+)=(.+):(.+?)(@.+)?\"$"
	);
	
	public final String name, modid, id;
	public final int meta;
	
	public BlockDef(String string) {
		Matcher m = PATTERN.matcher(string);
		checkArgument(m.matches());
		name = m.group(1).trim();
		modid = m.group(2).trim();
		id = m.group(3).trim();
		meta = parseMeta(firstNonNull(m.group(4), "@0").substring(1).trim());
		
		this.string = string;
	}
	
	private static int parseMeta(String meta) {
		return meta.equals("W") ? W : Integer.parseInt(meta);
	}
	
	
	public boolean isModLoaded() {
		return modid.equals("minecraft") || Loader.isModLoaded(modid);
	}
	
	@Override public boolean exists() {
		Optional<Block> block = Utils.getBlock(modid, id);
		if (block.isPresent()) {
			data = new BlockData(block.get(), meta);
			return true;
		}
		return false;
	}
	
	private BlockData data;
	@Override public BlockData get() {
		if (!exists())
			throw new IllegalStateException();
		return data;
	}
	
	public String getBlockDesc() {
		return modid + ":" + id + at(meta);
	}
	
	private String string;
	@Override public String toString() {
		return string != null ? string : (string = String.format(
				"\"%s = %s:%s%s\"",
				name, modid, id, at(meta)
		));
	}
	
	static String at(int meta) {
		switch (meta) {
		case 0:
			return "";
		case W:
			return "@W";
		default:
			return "@" + Integer.toString(meta);
		}
	}

}
