package jsettlers.logic.map.random.landscape;

public enum HillPolicy {
	FLAT,
	
	/**
	 * Just some noise
	 */
	NOISY,
	
	/**
	 * Some smaller hills
	 */
	HILLY,
	
	/**
	 * A Mountain => inclines to the center
	 */
	MOUNTAIN
}
