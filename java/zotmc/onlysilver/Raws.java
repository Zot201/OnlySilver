package zotmc.onlysilver;

import java.util.List;
import java.util.Map;

public class Raws {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <K, V> Map<K, V> castRaw(Map map) {
		return map;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <E> List<E> castRaw(List list) {
		return list;
	}
	
}
