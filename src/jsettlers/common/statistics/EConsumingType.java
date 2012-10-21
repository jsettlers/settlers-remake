package jsettlers.common.statistics;

/**
 * This defines things that can consume materials.
 * 
 * @author michael
 */
public enum EConsumingType {
	/**
	 * A building that uses the material to work.
	 * 
	 * @see IConsumingBuildingType
	 */
	WORKING_BUILDING,

	/**
	 * The material needed for constructing buildings.
	 */
	CONSTRUCTION,

	/**
	 * The material needed for recruiting new soldiers or workers. NOTE: If we
	 * want to, we can distinguish the type of worker here.
	 */
	RECRUITION
}
