package eu.mapperproject.xmml.topology;

import java.util.Map;

import eu.mapperproject.xmml.util.graph.Child;
import java.util.ArrayList;
import java.util.List;

/**
 * A natural domain of a process, hierarchically ordered
 * @author Joris Borgdorff
 *
 */
public class Domain implements Child<Domain>, Comparable<Domain> {
	private final String name;
	private final Domain parent;
	
	/** A generic root domain denoting all domains */
	public final static Domain GENERIC = new Domain("GENERIC");
	
	public Domain(String name) {
		this(name, null);
	}
	
	private Domain(String name, Domain parent) {
		this.parent = parent;
		this.name = name;
	}
	
	/** Create a new domain which is a child of the current domain */
	public Domain getChild(String name) {
		return new Domain(name, this);
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
		Domain other = (Domain)o;
		return this.name.equals(other.name) && (this.parent == null ? other.parent == null : this.parent.equals(other.parent));
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + this.name.hashCode();
		hash = 79 * hash + (this.parent != null ? this.parent.hashCode() : 0);
		return hash;
	}
	
	/**
	 * Parse a domain and their parents from a string.
	 * 
	 * The string should be a dot-delimited sequence of domains, first one being the root
	 * @param s parseable string
	 * @param domains a map of previously parsed domains, which can be used as parents. This will be extended with new domains found
	 */
	public static Domain parseDomain(String s, Map<String, List<Domain>> domains) {
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
				child = parent.getChild(ss[i]);
				peers.add(child);
			}

			// Proceed to the next level
			parent = child;
		}

		return child;
	}

	@Override
	public int compareTo(Domain t) {
		return name.compareTo(t.name);
	}
}
