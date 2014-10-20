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
	 * @return true if the position should be removed from the unflattenener.<br>
	 *         false if {@link #countFlattenedDown(short, short)} should be called again for this position.
	 */
	boolean countFlattenedDown(short x, short y);

}
