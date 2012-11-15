package jsettlers.logic.algorithms.partitions;

import jsettlers.common.position.ShortPoint2D;

/**
 * Interface for a tester class needed for the {@link PartitionsAlgorithm}. <br>
 * Implementors of this class are used to determine if two points on the border of a partition are still connected by the partition.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IPartitionDividedTester {

	/**
	 * Tests if the given positions (that need to lie on the border of the given partition) are connected by the given partition.
	 * 
	 * @param pos1
	 *            The first position.
	 * @param pos2
	 *            The second position.
	 * @param partition
	 *            The partition of both positions.
	 * @return true if both positions are connected by the given partition.<br>
	 *         false if the positions are not connected.
	 */
	boolean isPartitionNotDivided(ShortPoint2D pos1, ShortPoint2D pos2, short partition);

}
