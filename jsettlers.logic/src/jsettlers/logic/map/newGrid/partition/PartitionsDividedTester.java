package jsettlers.logic.map.newGrid.partition;

import jsettlers.algorithms.interfaces.IContainingProvider;
import jsettlers.algorithms.traversing.ITraversingVisitor;
import jsettlers.algorithms.traversing.borders.BorderTraversingAlgorithm;
import jsettlers.common.position.ShortPoint2D;

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
	public static boolean isPartitionDivided(final short[] partitionRepresentatives,
			final short[] partitions, final short width,
			final ShortPoint2D pos1, final ShortPoint2D pos2, final short partition) {

		return posNotOnBorder(partitionRepresentatives, partitions, width, pos1, pos2, partition)
				&& posNotOnBorder(partitionRepresentatives, partitions, width, pos2, pos1, partition);
	}

	/**
	 * NOTE: The call to this method is different if the given positions are swaped!
	 * 
	 * @param debugColorSetable
	 * @param partitionRepresentatives
	 * @param partitions
	 * @param width
	 * @param pos1
	 * @param pos2
	 * @param partition
	 * @return
	 */
	private static boolean posNotOnBorder(final short[] partitionRepresentatives,
			final short[] partitions, final short width, final ShortPoint2D pos1, final ShortPoint2D pos2, final short partition) {
		final short pos2X = pos2.x;
		final short pos2Y = pos2.y;

		boolean pos2NotOnBorder = BorderTraversingAlgorithm.traverseBorder(new IContainingProvider() {
			@Override
			public boolean contains(int x, int y) {
				return partitionRepresentatives[partitions[x + y * width]] == partition;
			}
		}, pos1, new ITraversingVisitor() {
			@Override
			public boolean visit(int x, int y) {
				return pos2X != x || pos2Y != y;
			}
		}, false);
		return pos2NotOnBorder;
	}
}
