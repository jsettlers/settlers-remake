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
public enum EAction {
	/**
	 * No action is performed and the settler is just standing.
	 */
	NO_ACTION,

	/**
	 * The settler is walking from one tile to an other.
	 */
	WALKING,
	/**
	 * The settler picks up something
	 */
	TAKE,
	/**
	 * The settler drops something
	 */
	DROP,
	/**
	 * A costume action, see above.
	 */
	ACTION1,
	/**
	 * A costume action, see above.
	 */
	ACTION2,

}
