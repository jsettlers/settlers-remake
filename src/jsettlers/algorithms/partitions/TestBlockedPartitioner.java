package jsettlers.algorithms.partitions;

import jsettlers.logic.algorithms.partitions.BlockedPartitioner;

public class TestBlockedPartitioner {
	public static void main(String args[]) {
		TestBlockedPartitionerGrid grid = new TestBlockedPartitionerGrid();
		grid.print();

		BlockedPartitioner partitioner = new BlockedPartitioner(grid, grid.getWidth(), grid.getHeight());
		partitioner.calculate();
		partitioner.print();
	}
}
