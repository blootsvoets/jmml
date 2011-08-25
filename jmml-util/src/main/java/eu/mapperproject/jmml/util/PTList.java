package eu.mapperproject.jmml.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PTList {
	public static <P,Q> List<Q> add(P key, Q value, Map<P,List<Q>> map) {
		List<Q> l = get(key, map);
		l.add(value);
		return l;
	}

	public static <P,Q> List<Q> get(P key, Map<P,List<Q>> map) {
		List<Q> col = map.get(key);
		if (col == null) {
			col = new ArrayList<Q>();
			map.put(key, col);
		}
		return col;
	}
	
	public static <Q> List<Q> get(int key, List<List<Q>> list) {
		while (key >= list.size()) {
			list.add(new ArrayList<Q>());
		}
		return list.get(key);
	}
	
	public static <P,Q,R> Map<Q,R> getMap(P key, Map<P,Map<Q,R>> map) {
		Map<Q,R> nm = map.get(key);
		if (nm == null) {
			nm = new ArrayMap<Q,R>();
			map.put(key, nm);
		}
		return nm;
	}

	public static <Q,R> Map<Q,R> getMap(int key, List<Map<Q,R>> list) {
		while (key >= list.size()) {
			list.add(new ArrayMap<Q,R>());
		}
		return list.get(key);
	}
	
	public static <P,Q> Collection<Q> getSet(P key, Map<P, Collection<Q>> map) {
		Collection<Q> nm = map.get(key);
		if (nm == null) {
			nm = new ArraySet<Q>(4);
			map.put(key, nm);
		}
		return nm;		
	}
}
