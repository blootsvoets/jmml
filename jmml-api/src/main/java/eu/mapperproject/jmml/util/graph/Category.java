package eu.mapperproject.jmml.util.graph;

import eu.mapperproject.jmml.specification.Domain;
import eu.mapperproject.jmml.specification.annotated.AnnotatedDomain;
import eu.mapperproject.jmml.specification.util.ArrayMap;
import eu.mapperproject.jmml.specification.util.ArraySet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** A hierarchical category that any object can belong to.
 *
 *
 * @author Joris Borgdorff
 */ 
public class Category implements Child<Category> {
	/** A non-category, for specifying anything uncategorized */
	public final static Category NO_CATEGORY = new Category("None", new String[0]);

	private final static Map<Domain,Category> categoriesDomain;
	static {
		categoriesDomain = new ArrayMap<Domain,Category>();
		categoriesDomain.put(null, NO_CATEGORY);
	}

	private final String[] ancenstorNames;
	private final Category parent;
	private final String name;
	
	private Category(String name, String[] ancestorNames) {
		this.name = name;
		this.ancenstorNames = ancestorNames;
		if ("None".equals(name)) {
			this.parent = null;
		}
		else {
			this.parent = ancestorNames.length == 0 ? NO_CATEGORY : getCategory(ancestorNames);
		}
	}

	/**
	 * Get a category based on a domain.
	 * This implementation is cached, so calling this method with two equal Domain objects
	 * twice only create one Category objects.
	 * @throws NullPointerException if dom is null
	 */
	public static Category getCategory(AnnotatedDomain dom) {
		Category c = categoriesDomain.get(dom);
		if (c != null) return c;
		
		String name = dom.getName();
		AnnotatedDomain child = dom;
		
		List<String> ancestors = new ArrayList<String>();
		while (!child.isRoot()) {
			child = child.parent();
			ancestors.add(child.getName());
		}

		int len = ancestors.size();

		String[] theseNames = new String[len + 1];
		for (int i = 0; i < len; i++) {
			theseNames[len - i - 1] = ancestors.get(i);
		}
		theseNames[len] = name;

		c = getCategory(theseNames);
		categoriesDomain.put(dom, c);
		return c;
	}

	/** Get a category based on the hierarchy of categories.
	 * An array of category names gives all ancestors of the requested category
	 * and as final element the category name itself. This implementation is
	 * cached.
	 * @throws NullPointerException if given array is null.
	 */
	private static Category getCategory(String[] theseNames) {
		int len = theseNames.length - 1;

		String[] ancenstorNames = Arrays.copyOf(theseNames, len);
		Category c = new Category(theseNames[len], ancenstorNames);
		return c;
	}


	/** Get the name of this category, without parents */
	public String getName() {
		return this.name;
	}

	// Child
	@Override
	public boolean isRoot() {
		return this.parent == null;
	}
	
	@Override
	public Category parent() {
		if (this.isRoot()) {
			throw new IllegalStateException("Parent of root does not exist.");
		}
		
		return this.parent;
	}

	// Comparable
	@Override
	public int compareTo(Category t) {
		return this.name.compareTo(t.name);
	}

	// Object
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Category c = (Category)o;
		return this.name.equals(c.name) && Arrays.equals(this.ancenstorNames, c.ancenstorNames);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 47 * hash + Arrays.hashCode(this.ancenstorNames);
		hash = 47 * hash + this.name.hashCode();
		return hash;
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
}
