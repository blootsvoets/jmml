package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.Domain;
import eu.mapperproject.jmml.util.graph.Child;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Joris Borgdorff
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "domain", propOrder = {
    "value"
})
public class AnnotatedDomain extends Domain implements Child<AnnotatedDomain> {
	private transient AnnotatedDomain parent;
	
	public AnnotatedDomain() {
		super();
		this.parent = null;
	}
	
	private AnnotatedDomain(String[] names, int level) {
		this.value = names[level];
		this.parent = getDomain(names, level - 1);
	}
	
	
    /**
     * 
     * 	    A domain and all its super-domains, separated by periods like
	 *	       `domain.subdomain.subsubdomain'.
     * 	  
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */	
	@Override
    public String getValue() {
		if (this.parent == null && this.value.contains(".")) this.setValue(value); 
		if (this.isRoot()) {
			return this.value;
		}
		else {
			return this.parent.getValue() + '.' + this.value;
		}
    }
	
	@Override
	public String getName() {
		if (this.parent == null && this.value.contains(".")) this.setValue(value);
		return this.value;
	}

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	@Override
    public void setValue(String value) {
		String[] names = value.split("\\.");
		
		if (!this.matches(names, names.length - 1)) {
			this.value = names[names.length - 1];
			this.parent = getDomain(names, names.length - 2);
			addDomain(this, names.length - 1);
		}
    }
	
	/**
	 * Wheter a array of strings matches the current domain on the specified
	 * level.
	 * @param names
	 * @param level
	 * @return 
	 */
	private boolean matches(String[] names, int level) {
		if (level == 0) {
			return names[0].equals(this.value);
		}
		else {
			return names[level].equals(this.value)
				&& this.parent != null
				&& this.parent.matches(names, level - 1);
		}
	}
	
	@Override
	public AnnotatedDomain parent() {
		return this.parent;
	}

	@Override
	public boolean isRoot() {
		return this.parent == null;
	}

	
	private final static List<List<AnnotatedDomain>> DOMAINS;
	static {
		DOMAINS = new ArrayList<List<AnnotatedDomain>>();
	}

	public static AnnotatedDomain getDomain(String[] names, int level) {
		if (level < 0) {
			return null;
		}
		
		if (DOMAINS.size() > level) {
			for (AnnotatedDomain domain : DOMAINS.get(level)) {
				if (domain.matches(names, level)) {
					return domain;
				}
			}
		}
		
		AnnotatedDomain domain = new AnnotatedDomain(names, level);
		addDomain(domain, level);
		return domain;
	}
	
	private static void addDomain(AnnotatedDomain domain, int level) {
		while (DOMAINS.size() <= level) {
			DOMAINS.add(new ArrayList<AnnotatedDomain>());
		}
		DOMAINS.get(level).add(domain);
	}
}
