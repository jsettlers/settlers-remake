package jsettlers.logic.algorithms.landmarks;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.interfaces.IContainingProvider;
import jsettlers.logic.algorithms.traversing.ITraversingVisitor;
import jsettlers.logic.algorithms.traversing.area.AreaTraversingAlgorithm;
import jsettlers.logic.algorithms.traversing.borders.BorderTraversingAlgorithm;

/**
 * Thread to correct the landmarks. For example if Pioneers set all landmarks around a lake, this Thread will recognize it and take over the area of
 * the lake.
 * 
 * @author Andreas Eberle
 * 
 */
public final class EnclosedBlockedAreaFinderAlgorithm {

	public static final void checkLandmark(IEnclosedBlockedAreaFinderGrid grid, IContainingProvider containingProvider, ShortPoint2D startPos) {
		final short startX = startPos.x;
		final short startY = startPos.y;

		if (grid.isBlocked(startX, startY))
			return;

		short startPartition = grid.getPartitionAt(startPos.x, startPos.y);

		for (EDirection currDir : EDirection.values) {
			ShortPoint2D currPos = currDir.getNextHexPoint(startX, startY);

			if (grid.isBlocked(currPos.x, currPos.y)) {
				if (needsRelabel(grid, containingProvider, currPos, startPartition)) {
					// System.out.println("relabel needed at " + currX + "|" + currY + " with startPartition: " + startPartition);
					relabel(grid, containingProvider, currPos, startPartition);
				}
			}
		}
	}

	private static void relabel(final IEnclosedBlockedAreaFinderGrid grid, IContainingProvider containingProvider, ShortPoint2D blockedStartPos,
			final short newPartition) {
		ITraversingVisitor visitor = new ITraversingVisitor() {
			@Override
			public boolean visit(int x, int y) {
				grid.setPartitionAt(x, y, newPartition);
				return true;
			}
		};
		AreaTraversingAlgorithm.traverseArea(containingProvider, visitor, blockedStartPos, grid.getWidth(), grid.getHeight());
	}

	/**
	 * Checks if the blocked partition given by the coordinates blockedX and blockedY is surrounded by the given partition.
	 * 
	 * @param containingProvider2
	 * @param grid2
	 * 
	 * @param blockedX
	 * @param blockedY
	 * @param partition
	 * @param blockedStartPos
	 * @return
	 */
	private static boolean needsRelabel(final IEnclosedBlockedAreaFinderGrid grid, IContainingProvider containingProvider,
			ShortPoint2D blockedStartPos,
			final short partition) {
		return BorderTraversingAlgorithm.traverseBorder(containingProvider, blockedStartPos, new ITraversingVisitor() {
			@Override
			public boolean visit(int x, int y) {
				return grid.getPartitionAt((short) x, (short) y) == partition;
			}
		}, true);
	}
}
