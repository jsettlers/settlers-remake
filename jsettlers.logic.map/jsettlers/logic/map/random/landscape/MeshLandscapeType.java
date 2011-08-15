package jsettlers.logic.map.random.landscape;

public enum MeshLandscapeType {
	UNSPECIFIED,
	
	GRASS,
	
	SEA,
	
	SAND,
	
	MOUNTAIN, DESERT;

	public static MeshLandscapeType parse(String string) {
	    return valueOf(string.toUpperCase());
    }

	/**
	 * Does not throw an exception.
	 * @param parameter The parameter
	 * @return The result or the default value
	 */
	public static MeshLandscapeType parse(String parameter, MeshLandscapeType defaultValue) {
		try {
			return parse(parameter);
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
    }
	
}
