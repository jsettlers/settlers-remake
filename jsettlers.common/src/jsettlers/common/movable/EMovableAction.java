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
package jsettlers.common.movable;

/**
 * This is the action a settler is currently doing.
 * <p>
 * The common actions NO_ACTION, WALKING, TAKE, DROP are there as constants, but there are also settler specific actions. For these, the Constants
 * ACTION1 and ACTION2 are reserved. Their meaning depends on the type of the settler:
 * <table>
 * <tr>
 * <th>movable type</th>
 * <th>ACTION1</th>
 * <th>ACTION2</th>
 * </tr>
 * <tr>
 * <td>LUMBERJACK</td>
 * <td>wave the axe</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>SAWMILLER</td>
 * <td>saw</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>STONECUTTER</td>
 * <td>cut stones</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>FISHER</td>
 * <td>fish a fish</td>
 * <td>unsuccessfully fish</td>
 * </tr>
 * <tr>
 * <td>PIONEER</td>
 * <td>take some land</td>
 * <td></td>
 * </tr>
 * </table>
 *
 * @author michael
 */
public enum EMovableAction {
	/**
	 * No action is performed and the settler is just standing.
	 */
	NO_ACTION,

	/**
	 * The settler is walking from one tile to an other.
	 */
	WALKING,
	/**
	 * A costume action, see above.
	 */
	ACTION1,
	/**
	 * A costume action, see above.
	 */
	ACTION2,

	/**
	 * The settler bends down
	 */
	BEND_DOWN,
	/**
	 * The settler raises up
	 */
	RAISE_UP,

	/**
	 * The settler removes his hands behind his back (activation HOMELESS state)
	 */
	HOMELESS1,
	/**
	 * The settler hands behind his back IDLE state (only in HOMELESS state, HOMELESS state still active)
	 */
	HOMELESS2,
	/**
	 * The settler shakes his head, sighing (only in HOMELESS state, HOMELESS state still active)
	 * after complating HOMELESS2 animation goto HOMELESS3 or stay in HOMELESS_IDLE state
	 */
	HOMELESS3,
	/**
	 * The settler kicks a pebbles on the ground (only in HOMELESS state, HOMELESS state still active)
	 * after complating HOMELESS3 animation goto HOMELESS4 or stay in HOMELESS_IDLE state
	 */
	HOMELESS4,
	/**
	 * The settler returns hands back (only in HOMELESS state, completion HOMELESS state)
	 */
	HOMELESS_IDLE,
}
