package jsettlers.logic.map.newGrid.flags;

/**
 * This interface defines a listener for changes on the blocking grid.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IBlockingChangedListener {
	/**
	 * This method needs to be called on every change of the blocking grid.
	 * 
	 * @param x
	 *            x coordinate of the changed position.
	 * @param y
	 *            y coordinate of the changed position.
	 * @param newBlockingValue
	 *            The new blocking state. True if the position is now blocked, false if it is now unblocked.
	 */
	void blockingChanged(int x, int y, boolean newBlockingValue);
}
