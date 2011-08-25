package eu.mapperproject.jmml.util;



/**
 *
 * @author Joris Borgdorff
 */
public abstract class Distinguisher<T,V> {
	protected T[] partition;
	
	public Distinguisher(T[] partition) {
		this.partition = partition;
	}
	
	public int getIndex(V element) {
		T type = extractType(element);
		return getTypeIndex(type);
	}
	
	public int getTypeIndex(T type) {
		for (int i = 0; i < partition.length; i++) {
			if (belongsToPartition(i, type)) {
				return i;
			}
		}
		return -1;
	}
	
	public abstract String getId(V element);

	protected abstract boolean belongsToPartition(int p, T type);

	protected abstract T extractType(V element);
}
