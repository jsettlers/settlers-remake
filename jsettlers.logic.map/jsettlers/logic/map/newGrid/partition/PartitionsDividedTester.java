package jsettlers.logic.map.newGrid.partition;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.interfaces.IContainingProvider;
import jsettlers.logic.algorithms.traversing.ITraversingVisitor;
import jsettlers.logic.algorithms.traversing.borders.BorderTraversingAlgorithm;

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
	 * @param pos1
	 *            The first position.
	 * @param pos2
	 *            The second position.
	 * @param partition
	 *            The partition of both positions.
	 * @return true if both positions are connected by the given partition.<br>
	 *         false if the positions are not connected.
	 */
	public static boolean isPartitionNotDivided(final PartitionsGrid grid, final ShortPoint2D pos1, final ShortPoint2D pos2, final short partition) {
		final short width = grid.width;
		final short pos2X = pos2.x;
		final short pos2Y = pos2.y;

		boolean pos2NotOnBorder = BorderTraversingAlgorithm.traverseBorder(new IContainingProvider() {
			@Override
			public boolean contains(int x, int y) {
				return grid.partitionRepresentatives[grid.partitions[x + y * width]] == partition;
			}
		}, pos1, new ITraversingVisitor() {
			@Override
			public boolean visit(int x, int y) {
				return pos2X != x || pos2Y != y;
			}
		}, false);

		return !pos2NotOnBorder;
	}
}
