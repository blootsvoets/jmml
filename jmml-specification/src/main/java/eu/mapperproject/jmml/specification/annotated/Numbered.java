package eu.mapperproject.jmml.specification.annotated;

import eu.mapperproject.jmml.specification.annotated.Identifiable;

/**
 * An interface for uniquely numbered classes.
 * @author Joris Borgdorff
 */
public interface Numbered extends Identifiable {
	/** Get the number of the current class */
	public int getNumber();
	
	public void setNumber(int num);
}
