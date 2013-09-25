package jsettlers.logic.constants;

/**
 * This class defines the constants of jsettlers.logic.
 * 
 * @author Andreas Eberle
 * 
 */
public final class Constants {

	/**
	 * private constructor, because no instances of this class can be created.
	 */
	private Constants() {
	}

	public static final float TILE_PATHFINDER_COST = 1.0f;

	public static final byte STACK_SIZE = 8;

	public static final short WIDTH = 400;
	public static final short HEIGHT = 700;

	public static final int MAX_STONE_SIZE = 14;

	public static final byte MOVABLE_INTERRUPTS_PER_SECOND = 17;

	public static final short MOVABLE_INTERRUPT_DELAY = 1000 / MOVABLE_INTERRUPTS_PER_SECOND;

	public static float MOVABLE_STEP_DURATION = 0.4f;

	public static final float MOVABLE_TURN_PROBABILITY = 0.003F;
	public static final float MOVABLE_NO_ACTION_NEIGHBOR_PUSH_PROBABILITY = 0.2F;

	public static final float MOVABLE_TAKE_DROP_DURATION = 0.5f;

	public static final short MOVABLE_VIEW_DISTANCE = 8;

	public static final short MOVABLE_FLOCK_TO_DECENTRALIZE_MAX_RADIUS = 2;

	public static final short MOVABLE_FLEE_TO_VALID_POSITION_RADIUS = 40;

	public static final short SOLDIER_SEARCH_RADIUS = 30;
	public static final short TOWER_SEARCH_RADIUS = 40;
	public static final int BOWMAN_ATTACK_RADIUS = 20;
	public static final int BOWMAN_IN_TOWER_ATTACK_RADIUS = TOWER_SEARCH_RADIUS;
	public static final int BOWMAN_MIN_ATTACK_DISTANCE = 5;

	/**
	 * interrupts until arrows are removed from the map again.<br>
	 * 50 seconds
	 */
	public static final short ARROW_DECOMPOSE_INTERRUPTS = 50 * MOVABLE_INTERRUPTS_PER_SECOND;

	public static final byte BRICKLAYER_ACTIONS_PER_MATERIAL = 12;

	public static final int TILES_PER_DIGGER = 15;

	public static final int PARTITION_MANANGER_RUNS_PER_TICK = 5;

	public static final float GHOST_PLAY_DURATION = 1;

	/**
	 * If the door is hit, its health is reduced by the hit strength / {@link #DOOR_HIT_RESISTENCY_FACTOR}
	 */
	public static final float DOOR_HIT_RESISTENCY_FACTOR = 2;

	public static final float TOWER_DOOR_REGENERATION = 0.01f;

	/**
	 * Defines the percentage used to calculate the payback of materials when a building is destroyed.<br>
	 * The formula defines that you get back {@value #BUILDINGS_DESTRUCTION_MATERIALS_PAYBACK_FACTOR} of the materials already invested in the
	 * building.
	 */
	public static final float BUILDINGS_DESTRUCTION_MATERIALS_PAYBACK_FACTOR = 0.5f;
	/**
	 * Defines the factor that is used to calculate the maximum allowed distance from a building to it's work area center. The maximum distance is
	 * calculated as the work radius of the building multiplied by this factor.
	 */
	public static final short BUILDINGS_MAX_WORKRADIUS_FACTOR = 4;

	/**
	 * This constant is used as a scale factor for the construction mark value calculation.
	 */
	public static final float CONSTRUCTION_MARK_SCALE_FACTOR = 5;
	/**
	 * This constant is used as a pow factor for the construction mark value calculation.
	 */
	public static final float CONSTRUCTION_MARK_POW_FACTOR = 1.5f;

}
