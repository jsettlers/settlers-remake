package jsettlers.common.material;

/**
 * This enum defines things that can be searched on the grid.
 * 
 * @author Andreas Eberle
 * 
 */
public enum ESearchType {
	/**
	 * Searches for foreign ground that is not protected by a tower. This search type is used by pioneers.
	 */
	UNENFORCED_FOREIGN_GROUND,

	/**
	 * Searches for own ground (for movables that need to walk back to their ground).
	 */
	VALID_POSITION,

	/**
	 * Searches for a tree that can be cut by a forester.
	 */
	CUTTABLE_TREE,

	CUTTABLE_STONE,

	PLANTABLE_TREE,

	PLANTABLE_CORN,

	CUTTABLE_CORN,

	FISHABLE,

	/**
	 * A river to get water from
	 */
	RIVER,

	ENEMY,

	NON_BLOCKED_OR_PROTECTED,

	SOLDIER_BOWMAN,
	SOLDIER_SWORDSMAN,
	SOLDIER_PIKEMAN,

	RESOURCE_SIGNABLE,

	/**
	 * Search type for thiefs to find a material on enemy ground.
	 */
	FOREIGN_MATERIAL,

}