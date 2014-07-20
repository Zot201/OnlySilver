package zotmc.onlysilver.oregen;

import java.util.Arrays;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class OreGenProfile {
	
	private final ImmutableList<OreGeneration> oreGens;
	private final ImmutableMap<String, BlockDef> dict;
	
	public OreGenProfile() {
		oreGens = ImmutableList.of();
		dict = ImmutableMap.of();
	}
	
	public OreGenProfile(String[] profile, String[] blockDefinitions) {
		dict = BlockDefinitions.parseDefinitions(blockDefinitions);
		oreGens = FluentIterable.from(Arrays.asList(profile))
				.transform(new Function<String, OreGeneration>() {
					@Override public OreGeneration apply(String input) {
						return OreGeneration.fromString(dict, input);
					}
				})
				.toList();
	}
	public OreGenProfile(String profile, String... blockDefinitions) {
		this(new String[] {profile}, blockDefinitions);
	}
	
	public void validateProfile() {
		for (BlockDef def : dict.values())
			if (def.isModLoaded() && !def.exists())
				throw new IllegalArgumentException(def.toString());
	}
	
	
	private final Map<Integer, ImmutableList<OreGeneration>> cache = Maps.newHashMap();
	public ImmutableList<OreGeneration> getOreGen(int dimensionID) {
		ImmutableList<OreGeneration> ret = cache.get(dimensionID);
		if (ret != null)
			return ret;
		
		ImmutableList.Builder<OreGeneration> b = ImmutableList.builder();
		for (OreGeneration oreGen : oreGens)
			if (oreGen.isApplicable(dimensionID))
				b.add(oreGen);
		ret = b.build();
		
		cache.put(dimensionID, ret);
		return ret;
	}
	
	public String[] oreGenStrings() {
		return FluentIterable.from(oreGens)
				.transform(Functions.toStringFunction())
				.toArray(String.class);
	}
	public String[] blockDefStrings() {
		return BlockDefinitions.toStringList(dict);
	}
	
	@Override public String toString() {
		return Joiner.on('\n').join(Iterables.concat(oreGens, dict.values()));
	}
	
}
