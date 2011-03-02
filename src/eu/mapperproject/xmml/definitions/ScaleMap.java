package eu.mapperproject.xmml.definitions;

import java.util.Map;

public class ScaleMap {
	private final Scale time;
	private final Map<String, Scale> space;
	private final Map<String, Scale> other;

	public ScaleMap(Scale time, Map<String, Scale> space, Map<String, Scale> other) {
		this.time = time;
		this.space = space;
		this.other = other;
	}
}
