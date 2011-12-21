package jsettlers.algorithms;

import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.landmarks.ILandmarksThreadMap;
import jsettlers.logic.algorithms.landmarks.LandmarksCorrectingThread;

public class LandmarksThreadTester {

	protected static final int WIDTH = 10;
	protected static final int HEIGHT = 10;
	private static Map map;
	private static LandmarksCorrectingThread thread;

	public static void main(String args[]) throws InterruptedException {
		map = new Map();
		thread = new LandmarksCorrectingThread(map);

		for (short x = 3; x < 6; x++) {
			for (short y = 5; y < 7; y++) {
				map.setBlocked(x, y, true);
			}
		}

		setPartition(2, 4, 1);
		setPartition(2, 5, 1);
		setPartition(2, 6, 1);
		setPartition(2, 7, 1);

		setPartition(6, 4, 1);
		setPartition(6, 5, 1);
		setPartition(6, 6, 1);
		setPartition(6, 7, 1);

		setPartition(3, 4, 1);
		setPartition(4, 4, 1);
		setPartition(5, 4, 1);

		setPartition(3, 7, 1);
		setPartition(4, 7, 1);
		setPartition(5, 7, 1);

		Thread.sleep(500);
		printMap(map);
	}

	private static void setPartition(int x, int y, int partition) {
		map.setPartitionAndPlayerAt((short) x, (short) y, (short) partition);
		ISPosition2D pos = new ShortPoint2D(x, y);
		thread.addLandmarkedPosition(pos);
	}

	private static void printMap(Map map) {
		for (short y = HEIGHT - 1; y >= 0; y--) {
			for (short x = 0; x < WIDTH; x++) {
				if (map.isBlocked(x, y)) {
					System.out.print("  b|" + map.getPartitionAt(x, y) + "  ");
				} else {
					System.out.print("   |" + map.getPartitionAt(x, y) + "  ");
				}
			}
			System.out.println();
		}
	}

	private static class Map implements ILandmarksThreadMap {
		short[][] partitions = new short[WIDTH][HEIGHT];
		boolean[][] blocked = new boolean[WIDTH][HEIGHT];

		@Override
		public void setPartitionAndPlayerAt(short x, short y, short partition) {
			this.partitions[x][y] = partition;
		}

		@Override
		public boolean isInBounds(short x, short y) {
			return 0 <= x && x < WIDTH && 0 <= y && y < HEIGHT;
		}

		@Override
		public boolean isBlocked(short x, short y) {
			return blocked[x][y];
		}

		@Override
		public short getPartitionAt(short x, short y) {
			return partitions[x][y];
		}

		void setBlocked(short x, short y, boolean blocked) {
			this.blocked[x][y] = blocked;
		}

	}
}
