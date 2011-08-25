package eu.mapperproject.jmml.specification.util;

import eu.mapperproject.jmml.util.Distinguisher;
import eu.mapperproject.jmml.util.Identifiable;

/**
 *
 * @author Joris Borgdorff
 */
public class DistinguishClass extends Distinguisher<Class<?>,Identifiable> {
	public DistinguishClass(Class[] clazzs) {
		super(clazzs);
	}

	@Override
	protected Class extractType(Identifiable element) {
		return element.getClass();
	}
	@Override
	protected boolean belongsToPartition(int p, Class<?> type) {
		return partition[p].isAssignableFrom(type);
	}
	@Override
	public String getId(Identifiable element) {
		return element.getId();
	}
}
