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
	private final VersionRange[] version;
	private final Version[] multiversion;
	
	/** Create a version or version range from a string */
	public Version(String version) {
		this.multiversion = null;
		String[] versions = version.split("\\.");
		this.version = new VersionRange[versions.length];
		for (int i = 0; i < versions.length; i++) {
			this.version[i] = new VersionRange(versions[i]);
		}
	}
	
	/** Create a version range from multiple strings */
	public Version(String[] versions) {
		this.version = null;
		this.multiversion = new Version[versions.length];
		
		for (int i = 0; i < versions.length; i++) {
			this.multiversion[i] = new Version(versions[i]);
		}
	}
	
	/**
	 * Whether given version is contained within the current one
	 * 
	 * If the version number given contains more elements, it is not contained in this range.
	 * @throws IllegalArgumentException if the version given is not a definite one
	 */
	public boolean contains(Version v) {
		// With multiple versions, delegate
		if (this.hasMultiple()) {
			for (Version version : this.multiversion) {
				if (version.contains(v)) return true;
			}
			return false;
		}
		
		if (!v.isDefinite()) {
			throw new IllegalArgumentException("Can not contain a version range in a version.");
		}
		
		boolean vIsMin = v.version.length < this.version.length;
		VersionRange[] max = vIsMin ? this.version : v.version;
		int minLength = vIsMin ? v.version.length : this.version.length;
		
		// Check all matching version numbers
		for (int i = 0; i < minLength; i++) {
			if (!this.version[i].contains(v.version[i].getMininum())) {
				return false;
			}
		}
		
		// Check if all version numbers after the last dot are equivalent (zero) 
		for (int i = minLength; i < max.length; i++) {
			if (!max[i].contains(0)) return false;
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
	
	/** Whether the version contains multiple ranges */
	private boolean hasMultiple() {
		return this.multiversion != null;
	}
	
	@Override
	public String toString() {
		if (this.hasMultiple() && this.multiversion.length > 1) {
			return "Versions " + versionString();
		}
		return "Version " + versionString();
	}
	
	/** Return each of the versions represented as a String */
	public String versionString() {
		StringBuilder builder = new StringBuilder();
		// With multiple versions, comma delimited string
		if (this.hasMultiple()) {
			for (Version v : this.multiversion) {
				builder.append(v.versionString());
				builder.append(", ");
			}
			int end = builder.length() - 1;
			builder.delete(end - 1, end);
		}
		// With a single version, dot delimited string
		else {
			for (VersionRange range : this.version) {
				builder.append(range);
				builder.append(".");
			}
			int end = builder.length() - 1;
			builder.deleteCharAt(end);
		}
		
		return builder.toString();
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
				if (version.charAt(0) == '[' && version.charAt(version.length() - 1) == ']') {
					version = version.substring(1, version.length() - 1);
				}
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
		
		/** Whether this range only contains a single version */
		boolean isAny() {
			return this.min == 0 && this.max == Integer.MAX_VALUE;
		}

		/** Returns the minimum value of the range */
		int getMininum() {
			return this.min;
		}
		
		/** Whether the range contains a version */
		boolean contains(int n) {
			return n >= min && n <= max;
		}
		
		@Override
		public String toString() {
			if (this.isAny()) return "x";
			else if (this.isSingle()) return String.valueOf(this.min);
			else return "[" + this.min + "-" + this.max + "]";
		}
	}
}
