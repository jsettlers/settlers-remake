package jsettlers.logic.map.newGrid.partition;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.partitions.IPartitionDividedTester;
import jsettlers.logic.algorithms.partitions.PartitionsAlgorithm;
import jsettlers.logic.algorithms.traversing.ITraversingVisitor;
import jsettlers.logic.algorithms.traversing.borders.BorderTraversingAlgorithm;
import jsettlers.logic.algorithms.traversing.borders.IContainingProvider;

/**
 * This class implements the {@link IPartitionDividedTester} interface and is used by the {@link PartitionsGrid} to supply the
 * {@link PartitionsAlgorithm} with the needed {@link IPartitionDividedTester}.
 * 
 * @author Andreas Eberle
 * 
 */
public class PartitionsDividedTester implements IPartitionDividedTester {
	private final short width;
	private final PartitionsGrid grid;

	public PartitionsDividedTester(short width, PartitionsGrid grid) {
		this.width = width;
		this.grid = grid;
	}

	@Override
	public boolean isPartitionNotDivided(final ShortPoint2D pos1, final ShortPoint2D pos2, final short partition) {
		final short pos2X = pos2.x;
		final short pos2Y = pos2.y;

		boolean pos2NotOnBorder = BorderTraversingAlgorithm.traverseBorder(new IContainingProvider() {
			@Override
			public boolean contains(int x, int y) {
				return grid.partitionRepresentative[grid.partitions[x + y * width]] == partition;
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
