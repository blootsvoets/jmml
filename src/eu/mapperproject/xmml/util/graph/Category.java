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
public class Category implements Child<Category>, StyledNode {
	/** A non-category, for specifying anything uncategorized */
	public final static Category NO_CATEGORY = new Category(Domain.MULTIPLE.toString(), new String[0]);

	private final String[] ancenstorNames;
	private Category parent;
	private final String name;
	
	
	private Category(String name, String[] ancestorNames) {
		this.name = name;
		this.ancenstorNames = ancestorNames;
	}
	
	public Category(Domain dom) {
		this.name = dom.toString();
		
		List<String> ancestors = new ArrayList<String>();
		while (!dom.isRoot()) {
			dom = dom.parent();
			ancestors.add(dom.toString());
		}
		if (!this.getName().equals(NO_CATEGORY.getName()) || ancestors.size() != 0) {
			ancestors.add(NO_CATEGORY.getName());
		}
		
		int len = ancestors.size();
		
		this.ancenstorNames = new String[len];
		for (int i = 0; i < len; i++) {
			this.ancenstorNames[len - i - 1] = ancestors.get(i); 
		}
	}
	
	@Override
	public boolean isRoot() {
		return this.ancenstorNames.length == 0;
	}
	
	@Override
	public Category parent() {
		if (this.isRoot())
			throw new IllegalStateException("Parent of root does not exist.");
		
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
		if (o == null || !this.getClass().equals(o.getClass())) return false;
		Category c = (Category)o;
		return this.name.equals(c.name) && Arrays.deepEquals(this.ancenstorNames, c.ancenstorNames);
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode() ^ Arrays.deepHashCode(this.ancenstorNames); 
	}

	@Override
	public String getStyle() {
		return null;
	}

	@Override
	public Category getCategory() {
		return this;
	}

	/* (non-Javadoc)
	 * @see eu.mapperproject.xmml.toolkit.graph.Node#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}
}
