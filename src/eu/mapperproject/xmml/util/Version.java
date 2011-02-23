/**
 * 
 */
package eu.mapperproject.xmml.util;

/**
 * Indicate a version or range of versions
 * 
 * @author Joris Borgdorff
 */
public class Version {
	private VersionRange[] version;
	
	/** Create a version or version range from a string */
	public Version(String version) {
		String[] versions = version.split(".");
		this.version = new VersionRange[versions.length];
		for (int i = 0; i < versions.length; i++) {
			this.version[i] = new VersionRange(versions[i]);
		}
	}
	
	/**
	 * Whether given version is contained within the current one
	 * 
	 * If the version number given contains more elements, it is not contained in this range.
	 * @throws IllegalArgumentException if the version given is not a definite one
	 */
	public boolean contains(Version v) {
		if (v.version.length > this.version.length) {
			return false;
		}
		if (!v.isDefinite()) {
			throw new IllegalArgumentException("Can not contain a version range in a version.");
		}
		
		for (int i = 0; i < v.version.length; i++) {
			if (!this.version[i].contains(v.version[i].getMininum())) {
				return false;
			}
		}
		
		return true;
	}
		
	/** Whether the version represents a single definite version */
	public boolean isDefinite() {
		for (VersionRange range : this.version) {
			if (!range.isSingle()) return false;
		}
		
		return true;
	}
	
	/** Indicate a version part range */
	private static class VersionRange {
		private final int min, max;
		
		/** A version number parsed from a string */
		VersionRange(String version) {
			if (version.equals("x")) {
				this.min = 0;
				this.max = Integer.MAX_VALUE;
			}
			else if (version.contains("-")) {
				String[] range = version.split("-");
				this.min = Integer.parseInt(range[0]);
				this.max = Integer.parseInt(range[1]);
			}
			else {
				this.min = Integer.parseInt(version);
				this.max = this.min;
			}
		}
		
		/** Whether this range only contains a single version */
		boolean isSingle() {
			return this.min == this.max;
		}
		
		/** Returns the minimum value of the range */
		int getMininum() {
			return this.min;
		}
		
		/** Whether the range contains a version */
		boolean contains(int n) {
			return n >= min && n <= max;
		}
	}
}
