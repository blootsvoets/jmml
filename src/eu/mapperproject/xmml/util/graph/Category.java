package eu.mapperproject.xmml.util.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.mapperproject.xmml.topology.Domain;

/** A hierarchical category that any object can belong to.
 *
 *
 * @author Joris Borgdorff
 */ 
public class Category implements Child<Category> {
	/** A non-category, for specifying anything uncategorized */
	public final static Category NO_CATEGORY = new Category(Domain.GENERIC.getName(), new String[0]);

	private final String[] ancenstorNames;
	private Category parent;
	private final String name;
	
	private Category(String name, String[] ancestorNames) {
		this.name = name;
		this.ancenstorNames = ancestorNames;
	}

	public static Category getCategory(Domain dom) {
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
		if (!ancestors.isEmpty()) {
			ancestors.add(NO_CATEGORY.name);
		}

		int len = ancestors.size();

		String[] ancenstorNames = new String[len];
		for (int i = 0; i < len; i++) {
			ancenstorNames[len - i - 1] = ancestors.get(i);
		}
		return new Category(name, ancenstorNames);
	}
	
	@Override
	public boolean isRoot() {
		return this.ancenstorNames.length == 0;
	}
	
	@Override
	public Category parent() {
		if (this.isRoot()) {
			throw new IllegalStateException("Parent of root does not exist.");
		}
		
		if (this.parent == null) {
			int newLen = this.ancenstorNames.length - 1;
			String newName = this.ancenstorNames[newLen];
			String[] newAnc = new String[newLen];
			System.arraycopy(this.ancenstorNames, 0, newAnc, 0, newLen);
			this.parent = new Category(newName, newAnc);
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
			return super.toString() + "." + this.name;
		}
	}

	/** Get the name of this category, without parents */
	public String getName() {
		return this.name;
	}
}
