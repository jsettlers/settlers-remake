package jsettlers.logic.map.newGrid.partition;

import jsettlers.algorithms.interfaces.IContainingProvider;
import jsettlers.algorithms.traversing.ITraversingVisitor;
import jsettlers.algorithms.traversing.borders.BorderTraversingAlgorithm;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.MutableInt;

/**
 * This class implements an algorithm used by the {@link PartitionsGrid} to check if two positions of a partition are divided.
 * 
 * @author Andreas Eberle
 * 
 */
final class PartitionsDividedTester {

	/**
	 * Private constructor to prevent the creation of objects.
	 */
	private PartitionsDividedTester() {
	}

	/**
	 * Tests if the given positions (that need to lie on the border of the given partition) are connected by the given partition.
	 * 
	 * @param debugColorSetable
	 * 
	 * @param partitionRepresentatives
	 * @param partitions
	 * @param width
	 * @param pos1
	 *            The first position.
	 * @param pos2
	 *            The second position.
	 * @param partition
	 *            The partition of both positions.
	 * @return true if both positions are connected by the given partition.<br>
	 *         false if the positions are not connected.
	 */
	public static boolean isPartitionDivided(short[] partitionRepresentatives, short[] partitions, short width, ShortPoint2D pos1,
			MutableInt partition1Size, ShortPoint2D pos2, MutableInt partition2Size, short partition) {

		return posNotOnBorder(partitionRepresentatives, partitions, width, pos1, pos2, partition, partition1Size)
				&& posNotOnBorder(partitionRepresentatives, partitions, width, pos2, pos1, partition, partition2Size);
	}

	/**
	 * NOTE: The call to this method is different if the given positions are swaped!
	 * 
	 * @param debugColorSetable
	 * @param partitionRepresentatives
	 * @param partitions
	 * @param width
	 * @param startPosition
	 * @param checkPosition
	 * @param partition
	 * @return
	 */
	private static boolean posNotOnBorder(final short[] partitionRepresentatives,
			final short[] partitions, final short width, final ShortPoint2D startPosition, final ShortPoint2D checkPosition, final short partition,
			MutableInt partitionSize) {
		final short checkPositionX = checkPosition.x;
		final short checkPositionY = checkPosition.y;

		boolean pos2NotOnBorder = BorderTraversingAlgorithm.traverseBorder(new IContainingProvider() {
			@Override
			public boolean contains(int x, int y) {
				return partitionRepresentatives[partitions[x + y * width]] == partition;
			}
		}, startPosition, new ITraversingVisitor() {
			@Override
			public boolean visit(int x, int y) {
				return checkPositionX != x || checkPositionY != y;
			}
		}, false, partitionSize);
		return pos2NotOnBorder;
	}
}
