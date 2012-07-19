package jsettlers.algorithms.partitions;

import jsettlers.logic.algorithms.partitions.IBlockedPartitionerGrid;

public class TestBlockedPartitionerGrid implements IBlockedPartitionerGrid {
	private final boolean[][] blocked = new boolean[][] {
			{ false, false, false, true, false, false, false, true, false, false },
			{ false, true, false, true, false, false, false, true, false, false },
			{ false, false, false, true, false, false, false, true, false, false },
			{ false, false, false, true, true, true, false, true, true, true },
			{ false, false, false, false, false, false, false, false, false, false },
			{ true, true, true, true, true, true, true, true, false, false },
			{ false, false, true, false, false, false, false, false, false, false },
			{ false, false, true, true, true, true, true, true, true, true }, //
			{ false, false, true, false, true, false, false, false, true, false },
			{ false, false, false, false, false, false, false, false, true, false } };

	public boolean isBlocked(int x, int y) {
		return blocked[y][x];
	}

	public void print() {
		for (int y = 0; y < getHeight(); y++) {
			for (int i = 0; i < 4 * (getHeight() - y); i++) {
				System.out.print(" ");
			}
			for (int x = 0; x < getWidth(); x++) {
				if (isBlocked(x, y)) {
					System.out.print("true    ");
				} else {
					System.out.print("false   ");
				}
			}
			System.out.println();
		}
	}

	public int getWidth() {
		return blocked[0].length;
	}

	public int getHeight() {
		return blocked.length;
	}
}
