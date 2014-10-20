package jsettlers.logic.map.random.landscape;

/**
 * This are the types of the landscapes on the landscape mesh.
 * @author michael
 *
 */
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
