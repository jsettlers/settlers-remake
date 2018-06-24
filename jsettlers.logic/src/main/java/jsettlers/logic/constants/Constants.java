/*******************************************************************************
 * Copyright (c) 2015 - 2018
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.constants;

/**
 * This class defines the constants of jsettlers.logic.
 *
 * @author Andreas Eberle
 *
 */
public final class Constants {

	public static final int MAX_FERRY_ENTRANCE_SEARCH_DISTANCE = 4;
	public static final int MAX_FERRY_ENTRANCE_DISTANCE        = 6;
	public static final int MAX_FERRY_UNLOADING_RADIUS         = 8;

	public static boolean FOG_OF_WAR_DEFAULT_ENABLED = true;

	public static final float COMBAT_STRENGTH_OWN_GROUND = 1;

	/**
	 * private constructor, because no instances of this class can be created.
	 */
	private Constants() {
	}

	public static final byte STACK_SIZE = 8;

	public static final byte MOVABLE_INTERRUPTS_PER_SECOND = 17;

	public static final short MOVABLE_INTERRUPT_PERIOD = 1000 / MOVABLE_INTERRUPTS_PER_SECOND;

	public static final short MOVABLE_BEND_DURATION = 500;

	public static final short MOVABLE_VIEW_DISTANCE = 8;

	public static final short MOVABLE_FLOCK_TO_DECENTRALIZE_MAX_RADIUS = 2;

	/**
	 * The radius within a tower can request soldiers to enter the building.
	 */
	public static final short TOWER_SEARCH_SOLDIERS_RADIUS = 100;

	/**
	 * The radius within soldiers search for targets to attack them.
	 */
	public static final short SOLDIER_SEARCH_RADIUS = 30;

	/**
	 * The radius within a a tower informs enemies
	 */
	public static final short TOWER_ATTACKABLE_SEARCH_RADIUS = 40;
	public static final int   BOWMAN_ATTACK_RADIUS           = 15;
	public static final int   BOWMAN_IN_TOWER_ATTACK_RADIUS  = 25;
	public static final int   BOWMAN_MIN_ATTACK_DISTANCE     = 7;

	public static final byte BRICKLAYER_ACTIONS_PER_MATERIAL = 12;

	public static final int TILES_PER_DIGGER = 15;

	public static final float GHOST_PLAY_DURATION = 1;

	/**
	 * If the door is hit, its health is reduced by the hit strength / {@link #DOOR_HIT_RESISTENCY_FACTOR}
	 */
	public static final float DOOR_HIT_RESISTENCY_FACTOR = 2;

	public static final float TOWER_DOOR_REGENERATION = 0.01f;

	/**
	 * Defines the percentage used to calculate the payback of materials when a building is destroyed.<br>
	 * The formula defines that you get back {@value #BUILDINGS_DESTRUCTION_MATERIALS_PAYBACK_FACTOR} of the materials already invested in the building.
	 */
	public static final float BUILDINGS_DESTRUCTION_MATERIALS_PAYBACK_FACTOR = 0.5f;
	/**
	 * Defines the factor that is used to calculate the maximum allowed distance from a building to it's work area center. The maximum distance is calculated as the work radius of the building
	 * multiplied by this factor.
	 */
	public static final short BUILDINGS_MAX_WORKRADIUS_FACTOR                = 4;

	/**
	 * This constant is used as a scale factor for the construction mark value calculation.
	 */
	public static final float CONSTRUCTION_MARK_SCALE_FACTOR = 2.5f;
	/**
	 * This constant is used as a pow factor for the construction mark value calculation.
	 */
	public static final float CONSTRUCTION_MARK_POW_FACTOR   = 1.5f;

	/**
	 * Maximum amount of resources per position.
	 */
	public static final byte MAX_RESOURCE_AMOUNT_PER_POSITION = 50;

	public static final short MOVABLE_FLEEING_DIJKSTRA_RADIUS = 9;
	public static final short MOVABLE_FLEEING_MAX_RADIUS      = TOWER_ATTACKABLE_SEARCH_RADIUS;

	public static int BUILDING_PLACEMENT_MAX_SEARCH_RADIUS = 3;
}
