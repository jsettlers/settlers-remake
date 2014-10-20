package jsettlers.buildingcreator.job;

public enum EBuildingJobType {
	/**
	 * Waits a given time.
	 * <p>
	 * Success: The time elapsed.
	 * <p>
	 * Fail: impossible.
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
	 * Searches a given search type.
	 * <p>
	 * Uses the special {@link BuildingSearchJob} class.
	 * <p>
	 * Success: The settler found the thing he should search and went to it.
	 * <p>
	 * Fail: If the searched thing was not found. The settler does not need to
	 * go back.
	 * @see BuildingSearchType
	 */
	SEARCH,
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
	 * Success: The settler looks at the given new direction.
	 * <p>
	 * Fail: impossible
	 */
	LOOK_AT,
	/**
	 * Plays an action animation.
	 * <p>
	 * Parameter: time - the time the action should take.
	 * <p>
	 * Success: The animation was played.
	 * <p>
	 * Fail: something was wrong...
	 */
	PLAY_ACTION1,
	/**
	 * @see EBuildingJobType#PLAY_ACTION1
	 */
	PLAY_ACTION2
}
