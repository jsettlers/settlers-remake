package jsettlers.common.buildings.jobs;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.ESearchType;

public enum EBuildingJobType {
	/**
	 * Waits a given time.
	 * <p>
	 * Parameter: time
	 * <p>
	 * Success: The time elapsed.
	 * <p>
	 * Fail: impossible.
	 * 
	 * @see BuildingJob#getTime();
	 */
	WAIT,

	/**
	 * Lets the settler walk in a given direction. The settler may wait.
	 * <p>
	 * Parameter: direction
	 * <p>
	 * Success: The settler is at the position
	 * <p>
	 * Fail: Should not happen normally.
	 */
	WALK,

	/**
	 * Shows the settler at a given position.
	 * <p>
	 * Parameter: dx, dy
	 * <p>
	 * Success: The settler appeared.
	 * <p>
	 * Fail: The settler could not appear at the given position.
	 */
	SHOW,

	/**
	 * Lets the settler disappear.
	 * <p>
	 * Parameter: none
	 * <p>
	 * Success: The settler disappeared instantly.
	 * <p>
	 * Fail: impossible
	 */
	HIDE,

	/**
	 * Sets the material property of the settler.
	 * <p>
	 * Parameter: material
	 * <p>
	 * Success: good
	 * <p>
	 * Fail: There was no given material at that position.
	 */
	SET_MATERIAL,

	/**
	 * Picks up the specified material. Does not change the material type
	 * assigned to the settler
	 * <p>
	 * Parameter: material
	 * <p>
	 * Success: There was a material at that position, one item was removed.
	 * <p>
	 * Fail: There was no given material at that position.
	 */
	TAKE,

	/**
	 * Lets the settler drop the given material to the stack at the positon.
	 * <p>
	 * Parameter: material
	 * <p>
	 * Success: When the settler dropped the material.
	 * <p>
	 * Fail: If the drop is impossible.
	 */
	DROP,

	/**
	 * Searches a given search type. The search center is given by the working
	 * center of the building.
	 * <p>
	 * Parameters: <br>
	 * search (type to be searched @see {@link ESearchType}), <br>
	 * dx, dy (position the movable will be showed later with SHOW)
	 * <p>
	 * This job always fails if the working radius is 0.
	 * <p>
	 * Success: A path to the searched thing has been found.
	 * <p>
	 * Fail: If the searched thing was not found.
	 * <p>
	 * XXX: what if an error occurs during walking there? (e.g. land ownership
	 * changed, object removed, ...)
	 * 
	 * @see ESearchType
	 * @see EBuildingType#getWorkradius()
	 */
	PRE_SEARCH,

	/**
	 * Searches a given search type with the InAreaFinder. The search center is
	 * given by the working center of the building.
	 * <p>
	 * Parameters: <br>
	 * search (type to be searched @see {@link ESearchType}), <br>
	 * dx, dy (position the movable will be showed later with SHOW)
	 * <p>
	 * This job always fails if the working radius is 0.
	 * <p>
	 * Success: A path to the searched thing has been found.
	 * <p>
	 * Fail: If the searched thing was not found.
	 * 
	 * @see ESearchType
	 * @see EBuildingType#getWorkradius()
	 */
	PRE_SEARCH_IN_AREA,

	/**
	 * Follows the pre-calculated path that has been searched with
	 * {@link #PRE_SEARCH} or {@link #PRE_SEARCH_IN_AREA}
	 */
	FOLLOW_SEARCHED,

	/**
	 * Goes to the position relative to the building.
	 * <p>
	 * Success: The settler is at the position
	 * <p>
	 * Fail: The position is unreachable.
	 */
	GO_TO,

	/**
	 * Look at
	 * <p>
	 * Parameter: direction
	 * <p>
	 * Success: The settler looks at the given new direction.
	 * <p>
	 * Fail: impossible
	 */
	LOOK_AT,

	/**
	 * Plays an action animation.
	 * <p>
	 * Parameter: time (the time the action should take)
	 * <p>
	 * Success: The animation was played.
	 * <p>
	 * Fail: something was wrong...
	 */
	PLAY_ACTION1,

	/**
	 * @see EBuildingJobType#PLAY_ACTION1
	 */
	PLAY_ACTION2,

	/**
	 * executes a command specified by a search option
	 * <p>
	 * Parameters: type ({@link jsettlers.common.material.ESearchType})
	 * <p>
	 * Success: the given search type has been executed
	 * <p>
	 * Fail: the given search type couldn't be executed
	 */
	EXECUTE,

	/**
	 * Tests whether there is a material at the given position.
	 * <p>
	 * Parameters: dx, dy, material
	 * <p>
	 * Success: There is material at that position.
	 * <p>
	 * Fail: There is no matching material at that position
	 */
	AVAILABLE,

	/**
	 * Tests if the stack at the position is full
	 * <p>
	 * Parameters: dx, dy, material
	 * <p>
	 * Success: The material may be placed at the given position
	 * <p>
	 * Fail: There is a full stack at that position, a wrong stack or it is
	 * blocked otherwise.
	 */
	NOT_FULL,

	/**
	 * Looks at the water that has been searched. TODO: make it work for other
	 * stuff, like Stones.
	 */
	LOOK_AT_SEARCHED,

	/**
	 * If the settler should be productive, this method succeds, it fails
	 * otherwise.
	 */
	IS_PRODUCTIVE
}
