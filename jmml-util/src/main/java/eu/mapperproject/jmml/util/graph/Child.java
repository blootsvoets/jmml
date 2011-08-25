package eu.mapperproject.jmml.util.graph;

/**
 * An interface for classes that have a parent
 * @author Joris Borgdorff
 *
 * @param <T> type of parent that child has
 */
public interface Child<T extends Child<T>> {
	public T parent();
	public boolean isRoot();
	public String getName();
}
