package jsettlers.logic.map.newGrid.landscape;

/**
 * Used to unflatten positions by {@link FlattenedResetter}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IFlattenedResettable {

	/**
	 * count down the flattened counter of the given position and eventually unflatten the position to grass again.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return true if the position was unflattened to grass again.<br>
	 *         false if it stayed flattened.
	 */
	boolean countFlattenedDown(short x, short y);

}
