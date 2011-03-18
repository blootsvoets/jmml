package eu.mapperproject.xmml.topology;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.mapperproject.xmml.util.graph.Child;

/**
 * A natural domain of a process, hierarchically ordered
 * @author Joris Borgdorff
 *
 */
public class Domain implements Child<Domain> {
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
	public int hashCode() {
		return this.parent.hashCode() ^ this.name.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Domain other = (Domain)o;
		return this.name.equals(other.name) && (this.parent == null ? other.parent == null : this.parent.equals(other.parent));
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
		// Try using existing domains. Once that has failed, it won't work again as their parents did not exist either.
		boolean useExisting = true;
		
		for (int i = 0; i < ss.length; i++) {
			child = null;
			List<Domain> list = domains.get(ss[i]);
			if (list == null) {
				list = new ArrayList<Domain>();
				domains.put(ss[i], list);
			}
			if (useExisting) {
				for (Domain d : list) {
					if (!d.isRoot() && d.parent().equals(parent)) {
						child = d;
					}
				}
			}
			if (child == null) {
				if (useExisting) useExisting = false;
				child = parent.getChild(ss[i]);
			}
			list.add(child);
			parent = child;
		}
		
		return child;
	}
}
