package jsettlers.common.mapobject;

public enum EMapObjectType {
	TREE_GROWING,
	TREE_ADULT,
	TREE_DEAD,
	CORN_GROWING,
	CORN_ADULT,
	CORN_DEAD,
	WAVES,
	STONE,
	/**
	 * This is the arrow object type.
	 * <p>
	 * Map objects with type arrow must implement {@link IArrowMapObject}
	 */
	ARROW,
	/**
	 * A Ghost (disappearing settler). TODO: Color by player
	 */
	GHOST,
	/**
	 * A working area mark. A progress of 0 means the outer ring, 1 means the inner ring. Currently, there are 4 rings.
	 */
	WORKAREA_MARK,
	/**
	 * The coal-mark for mountains. 0 means few coal, 1 is a lot.
	 */
	FOUND_COAL,
	FOUND_IRON,
	FOUND_GOLD,
	FOUND_GEMSTONE,
	FOUND_BRIMSTONE,
	FOUND_NOTHING,
	/**
	 * The sign that marks the center of a building site.
	 */
	BUILDINGSITE_SIGN,
	/**
	 * Building site borders.
	 */
	BUILDINGSITE_POST,

	/**
	 * in front of a door
	 */
	FLAG_DOOR,

	/**
	 * on top of the roof
	 */
	FLAG_ROOF
}
