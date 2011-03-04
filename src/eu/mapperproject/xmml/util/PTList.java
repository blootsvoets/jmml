package eu.mapperproject.xmml.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
			nm = new HashMap<Q,R>();
			map.put(key, nm);
		}
		return nm;
	}

	public static <Q,R> Map<Q,R> getMap(int key, List<Map<Q,R>> list) {
		while (key >= list.size()) {
			list.add(new HashMap<Q,R>());
		}
		return list.get(key);
	}
	
	public static <P,Q> Set<Q> getSet(P key, Map<P, Set<Q>> map) {
		Set<Q> nm = map.get(key);
		if (nm == null) {
			nm = new HashSet<Q>();
			map.put(key, nm);
		}
		return nm;		
	}
}
