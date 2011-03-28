package eu.mapperproject.jmml.definitions;

import java.util.Map;
import java.util.TreeMap;
/**
 * Stores and represents the scales of a submodel (instance)
 * @author Joris Borgdorff
 */
public class ScaleSet {
	private Map<String, Scale> all;
	private Scale time;
	private Map<String, Scale> space;
	private Map<String, Scale> other;

	public ScaleSet() {
		this.time = null;
		this.space = new TreeMap<String, Scale>();
		this.other = new TreeMap<String, Scale>();
		this.all = new TreeMap<String, Scale>();
	}

	public ScaleSet(Scale time, Map<String, Scale> space, Map<String, Scale> other) {
		this.time = time;
		this.space = space;
		this.other = other;
		this.all = new TreeMap<String, Scale>();
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
		case SPACE:
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
	
	/** Get the average number of timesteps using the current scale */
	public int getTimesteps() {
		return this.time.getSteps();
	}
	
	/** Copy references to each of the scales in the scale map to a new scale map */
	public ScaleSet copy() {
		ScaleSet copy = new ScaleSet();
		for (Scale s : this.all.values()) {
			copy.putScale(s);
		}
		return copy;
 	}
}
