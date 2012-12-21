package jsettlers.logic.map.newGrid.partition;

import java.util.LinkedList;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.Tuple;
import jsettlers.logic.algorithms.partitions.IBlockingProvider;
import jsettlers.logic.algorithms.traversing.ITraversingVisitor;

/**
 * This class implements the {@link ITraversingVisitor} and is used to detect all the partitions that are on the traversed positions.<br>
 * NOTE: This class is especially used for detecting necessary merges and divides when a position has changed player.
 * 
 * @author Andreas Eberle
 * 
 */
final class PartitionsListingBorderVisitor implements ITraversingVisitor {

	private final PartitionsGrid grid;
	private final IBlockingProvider blockingProvider;
	private final LinkedList<Tuple<Short, ShortPoint2D>> partitionsList = new LinkedList<Tuple<Short, ShortPoint2D>>();

	private short lastPartititon = -1;

	public PartitionsListingBorderVisitor(PartitionsGrid grid, IBlockingProvider blockingProvider) {
		this.grid = grid;
		this.blockingProvider = blockingProvider;
	}

	@Override
	public boolean visit(int x, int y) {
		if (blockingProvider.isBlocked(x, y)) {
			lastPartititon = -1;
		} else {
			short currPartition = grid.partitionRepresentatives[grid.partitions[x + y * grid.width]];

			if (currPartition != lastPartititon) {
				partitionsList.addLast(new Tuple<Short, ShortPoint2D>(currPartition, new ShortPoint2D(x, y)));
			}

			lastPartititon = currPartition;
		}
		return true;
	}

	public LinkedList<Tuple<Short, ShortPoint2D>> getPartitionsList() {
		if (partitionsList.size() >= 2 && partitionsList.getFirst().e1.equals(partitionsList.getLast().e1)) {
			partitionsList.removeFirst();
		}

		return partitionsList;
	}

}
