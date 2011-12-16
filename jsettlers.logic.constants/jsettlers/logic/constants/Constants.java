package jsettlers.logic.constants;

/**
 * some constants of jsettlers.logics
 * 
 * @author Andreas Eberle
 * 
 */
public final class Constants {

	public static final float TILE_PATHFINDER_COST = 1.0f;

	public static final float TILE_HEURISTIC_DIST = 1f;

	/**
	 * private constructor, because no instances of this class can be created.
	 */
	private Constants() {
	}

	public static final byte STACK_SIZE = 8;

	public static final short WIDTH = 400;
	public static final short HEIGHT = 700;

	public static final int MAX_STONE_SIZE = 14;

	public static final byte MOVABLE_INTERRUPTS_PER_SECOND = 17;

	public static final short MOVABLE_DELAY = 1000 / MOVABLE_INTERRUPTS_PER_SECOND;

	public static float MOVABLE_STEP_DURATION = 0.4f;

	public static final float MOVABLE_TURN_PROBABILITY = 0.06F;
	public static final float MOVABLE_NO_ACTION_NEIGHBOR_PUSH_PROBABILITY = 0.2F;

	public static final float MOVABLE_TAKE_DROP_DURATION = 0.5f;

	/**
	 * duration of the pioneer action in seconds
	 */
	public static final float PIONEER_ACTION_DURATION = 1.2f;

	public static final short BOWMAN_SEARCH_RADIUS = 16;

	public static final float BOWMAN_FIRE_RADIUS = 10;

	public static final short SOWRDSMAN_SEARCH_RADIUS = 13;

	/**
	 * interrupts until arrows are removed from the map again.<br>
	 * 50 seconds
	 */
	public static final short ARROW_DECOMPOSE_INTERRUPTS = 50 * MOVABLE_INTERRUPTS_PER_SECOND;

	public static final short PIKEMAN_SEARCH_RADIUS = 12;

	public static final byte BRICKLAYER_ACTIONS_PER_MATERIAL = 30;

	public static final int TILES_PER_DIGGER = 15;

	public static final short TOWER_SOLDIER_SEARCH_AREA = 45;

}
