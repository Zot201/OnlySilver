package zotmc.onlysilver.oregen;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Integer.parseInt;
import static zotmc.onlysilver.oregen.IntegerRangeSets.parseRanges;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.BoundType;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.google.common.hash.Hashing;

class OreGeneration {
	
	private static final Pattern PATTERN = Pattern.compile(
			"^\"(.+) of(.+)->(.+)=(.+) x(.+)x(.+)\"$"
	);
	public static OreGeneration fromString(Map<String, BlockDef> dict, String string) {
		try {
			Matcher m = PATTERN.matcher(string);
			checkArgument(m.matches());
			
			BlockDef base = dict.get(m.group(1).trim());
			ImmutableRangeSet<Integer> dimensions = parseRanges(m.group(2).trim());
			ImmutableRangeSet<Integer> heights = parseRanges(m.group(3).trim());
			BlockDef ore = dict.get(m.group(4).trim());
			int size = parseInt(m.group(5).trim());
			int rate = parseInt(m.group(6).trim());
			
			return new OreGeneration(ore, base, size, rate, heights, dimensions, string);
			
		} catch (Throwable e) {
			throw new IllegalArgumentException(
					String.format("Illegal Ore Generation Profile: %s", string), e);
		}
	}
	
	
	
	private static final Range<Integer> LIMIT = Range.closedOpen(0, 256);
	
	public final BlockDef ore, base;
	public final int size, rate;
	public final ImmutableRangeMap<Integer, Integer> heights;
	private final int sum;
	private final long bits;
	public final ImmutableRangeSet<Integer> dimensions;
	
	private OreGeneration(BlockDef ore, BlockDef base, int size, int rate,
			ImmutableRangeSet<Integer> heights, ImmutableRangeSet<Integer> dimensions) {
		this.ore = checkNotNull(ore);
		this.base = checkNotNull(base);
		this.size = checkPositive(size);
		this.rate = checkPositive(rate);
		
		int sum = 0;
		ImmutableRangeMap.Builder<Integer, Integer> builder = ImmutableRangeMap.builder();
		for (Range<Integer> r : heights.subRangeSet(LIMIT).asRanges()) {
			r = r.canonical(DiscreteDomain.integers());
			int diff = r.upperEndpoint() - r.lowerEndpoint();
			builder.put(Range.closedOpen(sum, sum += diff), r.lowerEndpoint());
		}
		this.sum = sum;
		this.heights = builder.build();
		
		bits = Hashing.md5().newHasher()
				.putString(ore.getBlockDesc(), Charsets.UTF_8)
				.putInt(this.heights.hashCode())
				.putString(base.getBlockDesc(), Charsets.UTF_8)
				.putInt(size)
				.putInt(rate)
				.hash().asLong();
		
		this.dimensions = checkNotNull(dimensions);
	}
	
	private OreGeneration(BlockDef ore, BlockDef base, int size, int rate,
			ImmutableRangeSet<Integer> heights, ImmutableRangeSet<Integer> dimensions, String string) {
		this(ore, base, size, rate, heights, dimensions);
		this.string = string;
	}
	
	private static int checkPositive(int i) {
		checkArgument(i >= 0);
		return i;
	}
	
	public boolean isApplicable(int dimensionID) {
		return dimensions.contains(dimensionID) && ore.exists() && base.exists();
	}
	
	private WorldGenerator worldGen;
	protected WorldGenerator worldGen() {
		return worldGen != null ? worldGen : (worldGen =
				new MetaMinable(ore.get(), size, base.get())
		);
	}
	
	public void generate(Random rand, int chunkX, int chunkZ, World world) {
		rand = new Random(rand.nextLong() ^ bits);
		
		for (int i = 0; i < rate; i++) {
			int x = chunkX * 16 + rand.nextInt(16);
			int z = chunkZ * 16 + rand.nextInt(16);
			
			int y = rand.nextInt(sum);
			Entry<Range<Integer>, Integer> r = heights.getEntry(y);
			y = y - r.getKey().lowerEndpoint() + r.getValue();
			
			worldGen().generate(world, rand, x, y, z);
		}
	}
	
	private String string;
	@Override public String toString() {
		return string != null ? string : (string = String.format(
				"\"%s of %s -> %s = %s x %d x %d\"",
				base.name,
				IntegerRangeSets.toString(dimensions),
				IntegerRangeSets.toString(heights),
				ore.name,
				size,
				rate
		));
	}

}
