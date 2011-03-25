package eu.mapperproject.jmml.topology;

import eu.mapperproject.jmml.util.Numbered;
import eu.mapperproject.jmml.util.graph.Child;

import java.util.Map;

import java.util.ArrayList;
import java.util.List;

/**
 * A natural domain of a process, hierarchically ordered
 * @author Joris Borgdorff
 *
 */
public class Domain implements Child<Domain>, Comparable<Domain>, Numbered {
	private final String name;
	private final Domain parent;
	private final int num;
	
	/** A generic root domain denoting all domains */
	public final static Domain GENERIC = new Domain(0, "GENERIC");
	
	public Domain(int num, String name) {
		this(num, name, null);
	}
	
	private Domain(int num, String name, Domain parent) {
		this.parent = parent;
		this.name = name;
		this.num = num;
	}
	
	/** Create a new domain which is a child of the current domain */
	public Domain getChild(int num, String name) {
		return new Domain(num, name, this);
	}
	
	@Override
	public boolean isRoot() {
		return this.parent == null;
	}
	
	@Override
	public Domain parent() {
		return this.parent;
	}
	
	@Override
	public String toString() {
		if (this.isRoot()) {
			return this.name;
		}
		else {
			return this.parent.toString() + "." + this.name;
		}
	}

	/** Get the name of this single domain */
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		return this.num == ((Domain)o).num;
	}

	@Override
	public int hashCode() {
		return this.num;
	}
	
	/**
	 * Parse a domain and their parents from a string.
	 * 
	 * The string should be a dot-delimited sequence of domains, first one being the root
	 * @param s parseable string
	 * @param domains a map of previously parsed domains, which can be used as parents. This will be extended with new domains found
	 */
	public static Domain parseDomain(String s, int startNum, Map<String, List<Domain>> domains) {
		String[] ss = s.split("\\.");
		Domain child = Domain.GENERIC, parent = Domain.GENERIC;
		
		for (int i = 0; i < ss.length; i++) {
			child = null;

			// Get all domains that have the same name but might be somewhere else in the hierarchy
			List<Domain> peers = domains.get(ss[i]);
			if (peers == null) {
				peers = new ArrayList<Domain>();
				domains.put(ss[i], peers);
			}

			// Determine which of the namesakes has the same place in the hierarchy
			for (Domain peer : peers) {
				if (!peer.isRoot() && peer.parent().equals(parent)) {
					child = peer;
					break;
				}
			}

			// If it was not found among its namesakes, create a new domain
			if (child == null) {
				child = parent.getChild(startNum++, ss[i]);
				peers.add(child);
			}

			// Proceed to the next level
			parent = child;
		}

		return child;
	}

	@Override
	public int compareTo(Domain t) {
		if (this.num < t.num) return -1;
		else if (this.num > t.num) return 1;
		else return 0;
	}

	@Override
	public int getNumber() {
		return this.num;
	}

	@Override
	public String getId() {
		return Integer.toString(this.num);
	}

	@Override
	public boolean deepEquals(Object o) {
		if (!this.equals(o)) return false;
		Domain other = (Domain)o;
		return this.name.equals(other.name) && (this.parent == null ? other.parent == null : this.parent.equals(other.parent));
	}
}
