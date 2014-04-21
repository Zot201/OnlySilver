package zotmc.onlysilver.handler;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.BoundType;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.google.common.hash.Hashing;

import cpw.mods.fml.common.IWorldGenerator;

public class OreGenerator implements IWorldGenerator {
	
	public final OreGenProfile profile;
	
	public OreGenerator(String[] profile, String[] blockDefinitions) {
		Map<String, Block> blocks = Maps.newHashMap();
		
		Splitter equal = Splitter.on('=').limit(2).trimResults();
		for (String line : blockDefinitions)
			try {
				List<String> list = equal.splitToList(line.substring(1, line.length() - 1));
				blocks.put(list.get(0), checkNotNull(Block.getBlockFromName(list.get(1))));
				
			} catch (Exception e) {
				throw new RuntimeException(String.format("Illegal Block Definition: %s", line), e);
			}
		
		this.profile = new OreGenProfile(profile, blocks);
	}
	
	
	public static class OreGenProfile {
		
		public final ImmutableMap<OrePlacer, ImmutableSortedSet<Integer>> data;
		
		public OreGenProfile(String[] rawData, Map<String, Block> blocks) {
			ImmutableMap.Builder<OrePlacer, ImmutableSortedSet<Integer>> data = ImmutableMap.builder();
			
			Pattern pattern = Pattern.compile("\"(.*) of(.*)->(.*)=(.*) x(.*)x(.*)\"");
			for (String line : rawData)
				try {
					Matcher m = pattern.matcher(line);
					checkArgument(m.matches());
					
					Block baseBlock = checkNotNull(blocks.get(m.group(1).trim()));
					RangeSet<Integer> dimensions = parseRanges(m.group(2));
					RangeSet<Integer> heightRanges = parseRanges(m.group(3));
					Block ore = checkNotNull(blocks.get(m.group(4).trim()));
					int veinSize = Integer.parseInt(m.group(5).trim());
					int rate = Integer.parseInt(m.group(6).trim());
					
					data.put(new OrePlacer(ore, veinSize, baseBlock, rate, heightRanges),
							ImmutableRangeSet.copyOf(dimensions).asSet(DiscreteDomain.integers()));
					
				} catch (Exception e) {
					throw new RuntimeException(String.format("Illegal Ore Generation Profile: %s", line), e);
				}
			
			this.data = data.build();
		}
		
		public static final Splitter
		SLASH = Splitter.on('\\'),
		COMMA = Splitter.on(',').trimResults(),
		INTERVAL_SEPARATORS = Splitter.onPattern("(\\.\\.|‥|,)").limit(2).trimResults();
		private static RangeSet<Integer> parseRanges(String s) {
			RangeSet<Integer> ret = TreeRangeSet.create();
			
			List<String> list = SLASH.splitToList(s);
			
			ret.addAll(parseRange(list.get(0)));
			for (int i = 1; i < list.size(); i++)
				ret.removeAll(parseRange(list.get(i)));
			
			return ret;
		}
		private static RangeSet<Integer> parseRange(String s) {
			s = s.trim();
			
			if (s.equals("ALL"))
				return ImmutableRangeSet.of(Range.<Integer>all());
			
			if (s.startsWith("{") && s.endsWith("}")) {
				RangeSet<Integer> ret = TreeRangeSet.create();
				for (String part : COMMA.split(s.substring(1, s.length() - 1)))
					ret.add(Range.singleton(Integer.parseInt(part)));
				return ret;
			}
			
			BoundType lowerType;
			switch (s.charAt(0)) {
			case '(':
				lowerType = BoundType.OPEN;
				break;
			case '[':
				lowerType = BoundType.CLOSED;
				break;
			default:
				throw new RuntimeException();
			}
			
			BoundType upperType;
			switch (s.charAt(s.length() - 1)) {
			case ')':
				upperType = BoundType.OPEN;
				break;
			case ']':
				upperType = BoundType.CLOSED;
				break;
			default:
				throw new RuntimeException();
			}
			
			
			List<String> list = INTERVAL_SEPARATORS.splitToList(s.substring(1, s.length() - 1));
			String s0 = list.get(0), s1 = list.get(1);
			
			if (s0.equals("-INF"))
				if (s1.equals("+INF"))
					return ImmutableRangeSet.of(Range.<Integer>all());
				else
					return ImmutableRangeSet.of(Range.upTo(Integer.parseInt(s1), upperType));
			else
				if (s1.equals("+INF"))
					return ImmutableRangeSet.of(Range.downTo(Integer.parseInt(s0), lowerType));
				else
					return ImmutableRangeSet.of(Range.range(
							Integer.parseInt(s0), lowerType,
							Integer.parseInt(s1), upperType));
		}
		
		
		
		private final Map<Integer, ImmutableList<OrePlacer>> cache = Maps.newHashMap();
		public ImmutableList<OrePlacer> getOrePlacers(int dimensionID) {
			ImmutableList<OrePlacer> ret = cache.get(dimensionID);
			
			if (ret == null) {
				ImmutableList.Builder<OrePlacer> builder = ImmutableList.builder();
				for (Map.Entry<OrePlacer, ImmutableSortedSet<Integer>> entry : data.entrySet())
					if (entry.getValue().contains(dimensionID))
						builder.add(entry.getKey());
				ret = builder.build();
				cache.put(dimensionID, ret);
			}
			
			return ret;
		}
		
		@Override public String toString() {
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<OrePlacer, ImmutableSortedSet<Integer>> entry : data.entrySet())
				sb.append(entry.getKey().toString()).append(" @ ").append(entry.getValue().toString()).append('\n');
			return sb.toString();
		}
		
	}
	
	
	public static class OrePlacer extends WorldGenMinable {
		
		private static final Range<Integer> LIMIT = Range.closedOpen(0, 256);
		
		public final Block ore, baseBlock;
		public final int veinSize, rate, sum;
		public final ImmutableRangeMap<Integer, Range<Integer>> ranges;
		
		public final long hashCode;
		
		public OrePlacer(Block ore, int veinSize, Block baseBlock, int rate, RangeSet<Integer> ranges) {
			super(ore, veinSize, baseBlock);
			this.ore = ore;
			this.baseBlock = baseBlock;
			this.veinSize = veinSize;
			this.rate = rate;
			
			int sum = 0;
			ImmutableRangeMap.Builder<Integer, Range<Integer>> builder = ImmutableRangeMap.builder();
			for (Range<Integer> r : ranges.asRanges()) {
				r = r.intersection(LIMIT).canonical(DiscreteDomain.integers());
				int diff = r.upperEndpoint() - r.lowerEndpoint();
				builder.put(Range.closedOpen(sum, sum += diff), r);
			}
			this.sum = sum;
			this.ranges = builder.build();
			
			
			hashCode = Hashing.md5().newHasher()
					.putString(getNameForBlock(baseBlock), Charsets.UTF_8)
					.putInt(this.ranges.asMapOfRanges().values().hashCode())
					.putString(getNameForBlock(ore), Charsets.UTF_8)
					.putInt(veinSize)
					.putInt(rate)
					.hash().asLong();
			
		}
		
		public void generateWithRate(Random rand, int chunkX, int chunkZ, World world) {
			rand = new Random(rand.nextLong() ^ hashCode);
			
			for (int i = 0; i < rate; i++) {
				int x = chunkX * 16 + rand.nextInt(16);
				int z = chunkZ * 16 + rand.nextInt(16);

				int y = rand.nextInt(sum);
				Entry<Range<Integer>, Range<Integer>> r = ranges.getEntry(y);
				y = y - r.getKey().lowerEndpoint() + r.getValue().lowerEndpoint();
				
				generate(world, rand, x, y, z);
			}
		}
		
		@Override public String toString() {
			return getNameForBlock(baseBlock) + " in " + ranges.asMapOfRanges().values()
					+ " => " + getNameForBlock(ore) + " x " + veinSize + " x " + rate;
		}
		
		@Override public int hashCode() {
			return Long.valueOf(hashCode).hashCode();
		}
		
	}
	
	private static String getNameForBlock(Block block) {
		return Block.blockRegistry.getNameForObject(block);
	}
	
	
	@Override public void generate(Random rand, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		
		for (OrePlacer p : profile.getOrePlacers(world.provider.dimensionId))
			p.generateWithRate(rand, chunkX, chunkZ, world);
	}
	
	@Override public String toString() {
		return profile.toString();
	}

}
