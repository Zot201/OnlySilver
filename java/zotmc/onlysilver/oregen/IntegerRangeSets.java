package zotmc.onlysilver.oregen;

import static com.google.common.collect.BoundType.CLOSED;
import static com.google.common.collect.BoundType.OPEN;
import static com.google.common.collect.DiscreteDomain.integers;

import java.util.List;
import java.util.Map.Entry;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.BoundType;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

class IntegerRangeSets {
	
	public static ImmutableRangeSet<Integer> all() {
		return ALL;
	}
	private static final ImmutableRangeSet<Integer> ALL = ImmutableRangeSet.of(Range.<Integer>all());
	
	
	private static final Splitter
	SLASH = Splitter.on('\\'),
	COMMA = Splitter.on(',').trimResults(),
	DOTS = Splitter.on("..").limit(2).trimResults();
	
	public static ImmutableRangeSet<Integer> parseRanges(String s) {
		RangeSet<Integer> ret = TreeRangeSet.create();
		
		List<String> list = SLASH.splitToList(s);
		
		ret.addAll(parseRange(list.get(0).trim()));
		for (int i = 1; i < list.size(); i++)
			ret.removeAll(parseRange(list.get(i).trim()));
		
		return ImmutableRangeSet.copyOf(ret);
	}
	
	private static RangeSet<Integer> parseRange(String s) {
		if (s.equals("ALL"))
			return all();
		
		if (s.startsWith("{") && s.endsWith("}")) {
			RangeSet<Integer> ret = TreeRangeSet.create();
			for (String part : COMMA.split(s.substring(1, s.length() - 1)))
				ret.add(Range.singleton(Integer.parseInt(part)));
			return ret;
		}
		
		BoundType lowerType;
		switch (s.charAt(0)) {
		case '(':
			lowerType = OPEN;
			break;
		case '[':
			lowerType = CLOSED;
			break;
		default:
			throw new IllegalArgumentException(s);
		}
		
		BoundType upperType;
		switch (s.charAt(s.length() - 1)) {
		case ')':
			upperType = OPEN;
			break;
		case ']':
			upperType = CLOSED;
			break;
		default:
			throw new IllegalArgumentException(s);
		}
		
		
		List<String> list = DOTS.splitToList(s.substring(1, s.length() - 1));
		String s0 = list.get(0), s1 = list.get(1);
		
		if (s0.equals("-INF"))
			if (s1.equals("+INF"))
				return all();
			else
				return ImmutableRangeSet.of(Range.upTo(Integer.parseInt(s1), upperType));
		else
			if (s1.equals("+INF"))
				return ImmutableRangeSet.of(Range.downTo(Integer.parseInt(s0), lowerType));
			else
				return ImmutableRangeSet.of(Range.range(
						Integer.parseInt(s0), lowerType,
						Integer.parseInt(s1), upperType
				));
	}
	
	
	
	
	public static String toString(RangeMap<Integer, Integer> mappedRanges) {
		TreeRangeSet<Integer> ret = TreeRangeSet.create();
		for (Entry<Range<Integer>, Integer> entry : mappedRanges.asMapOfRanges().entrySet()) {
			Range<Integer> r = entry.getKey().canonical(integers());
			int offset = entry.getValue();
			ret.add(Range.closedOpen(r.lowerEndpoint() + offset, r.upperEndpoint() + offset));
		}
		return toString(ret);
	}
	
	public static String toString(RangeSet<Integer> ranges) {
		Range<Integer> span = ranges.span().canonical(integers());
		RangeSet<Integer> diff = subtract(span, ranges);
		
		List<String> ret = Lists.newArrayList(toString(span));
		List<Integer> singletons = Lists.newArrayList();
		for (Range<Integer> r : diff.asRanges()) {
			if (isEmpty(r))
				continue;
			
			Optional<Integer> i = getSingletonValue(r);
			if (i.isPresent()) {
				singletons.add(i.get());
				continue;
			}
			
			if (singletons.size() > 0) {
				ret.add(toString(singletons));
				singletons.clear();
			}
			ret.add(toString(r));
		}
		if (singletons.size() > 0) {
			ret.add(toString(singletons));
			singletons.clear();
		}
		
		return Joiner.on('\\').join(ret);
	}
	
	private static RangeSet<Integer> subtract(Range<Integer> a, RangeSet<Integer> b) {
		TreeRangeSet<Integer> ret = TreeRangeSet.create();
		ret.add(a);
		ret.removeAll(b);
		return ret;
	}
	
	private static boolean isEmpty(Range<Integer> range) {
		return range.canonical(integers()).isEmpty();
	}
	
	private static Optional<Integer> getSingletonValue(Range<Integer> range) {
		range = range.canonical(integers());
		if (range.hasUpperBound()) {
			int value = range.lowerEndpoint();
			if (range.upperEndpoint() - value == 1)
				return Optional.of(value);
		}
		return Optional.absent();
	}
	
	
	private static final int NEG_INF =
			Range.<Integer>all().canonical(integers()).lowerEndpoint();
	
	private static String toString(Range<Integer> range) {
		range = range.canonical(integers());
		
		if (range.lowerEndpoint() == NEG_INF)
			if (!range.hasUpperBound())
				return "ALL";
			else
				return String.format("(-INF..%s)",
						range.upperEndpoint()
				);
		else
			if (!range.hasUpperBound())
				return String.format("[%s..+INF)",
						range.lowerEndpoint()
				);
			else
				return String.format("[%s..%s)",
						range.lowerEndpoint(), range.upperEndpoint()
				);
	}
	
	private static String toString(List<Integer> singletons) {
		return Joiner.on(", ")
				.appendTo(new StringBuilder("{"), singletons)
				.append('}')
				.toString();
	}
	

}
