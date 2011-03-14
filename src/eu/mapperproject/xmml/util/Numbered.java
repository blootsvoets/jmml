package eu.mapperproject.xmml.util;

import eu.mapperproject.xmml.Identifiable;

/**
 * An interface for uniquely numbered classes.
 * @author Joris Borgdorff
 */
public interface Numbered extends Identifiable {
	/** Get the number of the current class */
	public int getNumber();
}
