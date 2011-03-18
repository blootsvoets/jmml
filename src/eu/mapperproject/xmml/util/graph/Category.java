package eu.mapperproject.xmml.util.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.mapperproject.xmml.topology.Domain;
import java.util.HashMap;
import java.util.Map;

/** A hierarchical category that any object can belong to.
 *
 *
 * @author Joris Borgdorff
 */ 
public class Category implements Child<Category> {
	/** A non-category, for specifying anything uncategorized */
	public final static Category NO_CATEGORY = new Category(Domain.GENERIC.getName(), new String[0]);
	private final static Map<String[], Category> categories;
	private final static List<Category> categoriesDomain;
	static {
		categories = new HashMap<String[], Category>();
		categories.put(new String[] {NO_CATEGORY.name}, NO_CATEGORY);
		categoriesDomain = new ArrayList<Category>();
		categoriesDomain.add(NO_CATEGORY);
	}

	private final String[] ancenstorNames;
	private final Category parent;
	private final String name;
	
	private Category(String name, String[] ancestorNames) {
		this.name = name;
		this.ancenstorNames = ancestorNames;
		this.parent = ancestorNames.length == 0 ? null : getCategory(ancestorNames);
	}

	public static Category getCategory(Domain dom) {
		int num = dom.getNumber();
		while (categoriesDomain.size() <= num) {
			categoriesDomain.add(null);
		}

		Category c = categoriesDomain.get(num);
		if (c != null) return c;
		
		String name = dom.getName();
		if (name.equals(NO_CATEGORY.name)) {
			if (dom.isRoot()) {
				return NO_CATEGORY;
			}
			else {
				throw new IllegalArgumentException("May not instantiate another version of NO_CATEGORY");
			}
		}

		List<String> ancestors = new ArrayList<String>();
		while (!dom.isRoot()) {
			dom = dom.parent();
			ancestors.add(dom.getName());
		}

		int len = ancestors.size();

		String[] theseNames = new String[len + 1];
		for (int i = 0; i < len; i++) {
			theseNames[len - i - 1] = ancestors.get(i);
		}
		theseNames[len] = name;

		c = getCategory(theseNames);
		categoriesDomain.set(num, c);
		return c;
	}

	private static Category getCategory(String[] theseNames) {
		int len = theseNames.length - 1;

		if (categories.containsKey(theseNames)) {
			return categories.get(theseNames);
		}
		else {
			String[] ancenstorNames = Arrays.copyOf(theseNames, len);
			Category c = new Category(theseNames[len], ancenstorNames);
			categories.put(theseNames, c);
			return c;
		}
	}
	
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
	
	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Category c = (Category)o;
		return this.name.equals(c.name) && Arrays.equals(this.ancenstorNames, c.ancenstorNames);
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode() ^ Arrays.hashCode(this.ancenstorNames);
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

	/** Get the name of this category, without parents */
	public String getName() {
		return this.name;
	}

	@Override
	public int compareTo(Category t) {
		return this.name.compareTo(t.name);
	}
}
