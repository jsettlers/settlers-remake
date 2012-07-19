package jsettlers.logic.algorithms.partitions;

/**
 * Grid that can be used by {@link BlockedPartitioner} to calculate the partintioning of a grid.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IBlockedPartitionerGrid {
	/**
	 * Checks if the given position is blocked.
	 * 
	 * @param x
	 *            X coordinate of the position.
	 * @param y
	 *            Y coordinate of the position.
	 * @return true if the position is blocked<br>
	 *         false if it isn't blocked.
	 */
	boolean isBlocked(int x, int y);
}
