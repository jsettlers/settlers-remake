/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.buildingcreator.job;

public enum EBuildingJobType {
	/**
	 * Waits a given time.
	 * <p>
	 * SUCCESS: The time elapsed.
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
	 * SUCCESS: The settler is at the position
	 * <p>
	 * Fail: Should not happen normally.
	 */
	WALK,
	/**
	 * Shows the settler at a given position.
	 * <p>
	 * Parameter: dx, dy
	 * <p>
	 * SUCCESS: The settler appeared.
	 * <p>
	 * Fail: The settler could not appear at the given position.
	 */
	SHOW,
	/**
	 * Lets the settler disappear.
	 * <p>
	 * Parameter: none
	 * <p>
	 * SUCCESS: The settler disappeared instantly.
	 * <p>
	 * Fail: impossible
	 */
	HIDE,

	/**
	 * Sets the material property of the settler.
	 * <p>
	 * Parameter: material
	 * <p>
	 * SUCCESS: good
	 * <p>
	 * Fail: There was no given material at that position.
	 */
	SET_MATERIAL,

	/**
	 * Picks up the specified material. Does not change the material type assigned to the settler
	 * <p>
	 * Parameter: material
	 * <p>
	 * SUCCESS: There was a material at that position, one item was removed.
	 * <p>
	 * Fail: There was no given material at that position.
	 */
	TAKE,
	/**
	 * Lets the settler drop the given material to the stack at the positon.
	 * <p>
	 * Parameter: material
	 * <p>
	 * SUCCESS: When the settler dropped the material.
	 * <p>
	 * Fail: If the drop is impossible.
	 */
	DROP,
	/**
	 * Searches a given search type.
	 * <p>
	 * Uses the special {@link BuildingSearchJob} class.
	 * <p>
	 * SUCCESS: The settler found the thing he should search and went to it.
	 * <p>
	 * Fail: If the searched thing was not found. The settler does not need to go back.
	 * 
	 * @see BuildingSearchType
	 */
	SEARCH,
	/**
	 * Goes to the position relative to the building.
	 * <p>
	 * SUCCESS: The settler is at the position
	 * <p>
	 * Fail: The position is unreachable.
	 */
	GO_TO,

	/**
	 * Look at
	 * <p>
	 * SUCCESS: The settler looks at the given new direction.
	 * <p>
	 * Fail: impossible
	 */
	LOOK_AT,
	/**
	 * Plays an action animation.
	 * <p>
	 * Parameter: time - the time the action should take.
	 * <p>
	 * SUCCESS: The animation was played.
	 * <p>
	 * Fail: something was wrong...
	 */
	PLAY_ACTION1,
	/**
	 * @see EBuildingJobType#PLAY_ACTION1
	 */
	PLAY_ACTION2,
	PLAY_ACTION3
}
