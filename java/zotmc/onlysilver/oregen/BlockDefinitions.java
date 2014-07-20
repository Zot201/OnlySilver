package zotmc.onlysilver.oregen;

import java.util.Arrays;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;

class BlockDefinitions {
	
	public static ImmutableMap<String, BlockDef> parseDefinitions(String[] strings) {
		return FluentIterable.from(Arrays.asList(strings))
			.transform(new Function<String, BlockDef>() {
				@Override public BlockDef apply(String input) {
					return new BlockDef(input);
				}
			})
			.uniqueIndex(new Function<BlockDef, String>() {
				@Override public String apply(BlockDef input) {
					return input.name;
				}
			});
	}
	
	public static String[] toStringList(ImmutableMap<String, BlockDef> map) {
		return FluentIterable.from(map.values())
				.transform(Functions.toStringFunction())
				.toArray(String.class);
	}
	
}
