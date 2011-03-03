package eu.mapperproject.xmml.definitions;

import java.util.HashMap;
import java.util.Map;
/**
 * Stores and represents the scales of a submodel (instance)
 * @author Joris Borgdorff
 */
public class ScaleMap {
	private Map<String, Scale> all;
	private Scale time;
	private Map<String, Scale> space;
	private Map<String, Scale> other;

	public ScaleMap() {
		this.time = null;
		this.space = new HashMap<String, Scale>();
		this.other = new HashMap<String, Scale>();
		this.all = new HashMap<String, Scale>();
	}

	public ScaleMap(Scale time, Map<String, Scale> space, Map<String, Scale> other) {
		this.time = time;
		this.space = space;
		this.other = other;
		this.all = new HashMap<String, Scale>();
		this.all.put(time.getId(), time);
		this.all.putAll(space);
		this.all.putAll(other);
	}
	
	/** Put a scale in the scale map, replacing an old one with the same name */
	public void putScale(Scale s) {
		switch (s.getDimension()) {
		case TIME:
			time = s;
			break;
		case LENGTH:
			space.put(s.getId(), s);
			break;
		default:
			other.put(s.getId(), s);
			break;
		}
		
		this.all.put(s.getId(), s);
	}
	
	/** Get any scale by id. Returns null if the scale is not found */
	public Scale getScale(String id) {
		return this.all.get(id);
	}

	/** Whether a scale by the given ID already exists in this scale map */
	public boolean hasScale(String id) {
		return this.all.containsKey(id);
	}
	
	/** Copy references to each of the scales in the scale map to a new scale map */
	public ScaleMap copy() {
		ScaleMap copy = new ScaleMap();
		for (Scale s : this.all.values()) {
			copy.putScale(s);
		}
		return copy;
 	}
}
