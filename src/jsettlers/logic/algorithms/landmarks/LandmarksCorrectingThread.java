package jsettlers.logic.algorithms.landmarks;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.borders.traversing.BorderTraversingAlgorithm;
import jsettlers.logic.algorithms.borders.traversing.IBorderVisitor;
import jsettlers.logic.algorithms.borders.traversing.IContainingProvider;

/**
 * Thread to correct the landmarks. For example if Pioneers set all landmarks around a lake, this Thread will recognize it and take over the area of
 * the lake.
 * 
 * @author Andreas Eberle
 * 
 */
public final class LandmarksCorrectingThread extends Thread {
	private final ILandmarksThreadGrid grid;
	private final LinkedBlockingQueue<ShortPoint2D> queue = new LinkedBlockingQueue<ShortPoint2D>();
	private final IContainingProvider containingProvider;

	private boolean canceled;

	public LandmarksCorrectingThread(ILandmarksThreadGrid map) {
		super("LandmarksCorrectingThread");
		this.grid = map;
		this.containingProvider = new IContainingProvider() {
			@Override
			public boolean contains(int x, int y) {
				return grid.isBlocked((short) x, (short) y);
			}
		};

		this.setDaemon(true);
	}

	@Override
	public final void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		while (!canceled) {
			try {
				ShortPoint2D startPos = queue.take();

				if (startPos != null) {
					checkLandmarks(startPos);
				}
			} catch (InterruptedException e) {
			}
		}
	}

	private final void checkLandmarks(ShortPoint2D startPos) {
		final short startX = startPos.x;
		final short startY = startPos.y;

		if (grid.isBlocked(startX, startY))
			return;

		short startPartition = grid.getPartitionAt(startPos.x, startPos.y);

		for (EDirection currDir : EDirection.values) {
			short currX = (short) (startX + currDir.gridDeltaX);
			short currY = (short) (startY + currDir.gridDeltaY);

			if (grid.isBlocked(currX, currY)) {
				if (needsRelabel(currX, currY, startPartition)) {
					// System.out.println("relabel needed at " + currX + "|" + currY + " with startPartition: " + startPartition);
					relabel(grid.getBlockedPartition(startX, startY), currX, currY, startPartition);
				}
			}
		}
	}

	private void relabel(final short outsideBlockedPartition, final short blockedX, final short blockedY, final short newPartition) {

		BorderTraversingAlgorithm.traverseBorder(containingProvider, new ShortPoint2D(blockedX, blockedY), new IBorderVisitor() {
			int lastX = -1;
			int lastY = -1;

			@Override
			public boolean visit(int x, int y) {
				int dy = y - lastY;

				if (dy < 0 && lastY != -1) {
					relabelLine((short) lastX, (short) lastY, outsideBlockedPartition, newPartition);
					relabelLine((short) x, (short) y, outsideBlockedPartition, newPartition);
				}
				lastY = y;
				lastX = x;

				return true;
			}
		});
	}

	final void relabelLine(short startX, short startY, short outsideBlockedPartition, short newPartition) {

		// go left
		short x = startX;
		do {
			x--;

			final boolean isBlocked = grid.isBlocked(x, startY);

			if (isBlocked) {
				grid.setPartitionAndPlayerAt(x, startY, newPartition);
			} else {
				short currBlockedPartition = grid.getBlockedPartition(x, startY);
				if (currBlockedPartition == outsideBlockedPartition) {
					break;
				}
			}
		} while (x > 0); // prevent out of border

		// go right
		x = startX;
		final short highestX = (short) (grid.getWidth() - 1);
		do {
			x++;

			final boolean isBlocked = grid.isBlocked(x, startY);

			if (isBlocked) {
				grid.setPartitionAndPlayerAt(x, startY, newPartition);
			} else {
				short currBlockedPartition = grid.getBlockedPartition(x, startY);
				if (currBlockedPartition == outsideBlockedPartition) {
					break;
				}
			}
		} while (x < highestX); // prevent out of border

	}

	/**
	 * Checks if the blocked partition given by the coordinates blockedX and blockedY is surrounded by the given partition.
	 * 
	 * @param blockedX
	 * @param blockedY
	 * @param partition
	 * @return
	 */
	private boolean needsRelabel(short blockedX, short blockedY, final short partition) {
		return BorderTraversingAlgorithm.traverseBorder(containingProvider, new ShortPoint2D(blockedX, blockedY), new IBorderVisitor() {
			@Override
			public boolean visit(int x, int y) {
				return grid.getPartitionAt((short) x, (short) y) == partition;
			}
		});
	}

	public final void addLandmarkedPosition(ShortPoint2D pos) {
		queue.offer(pos);
	}

	public final void addLandmarkedPositions(List<ShortPoint2D> occupiedPositions) {
		queue.addAll(occupiedPositions);
	}

	public void cancel() {
		canceled = true;
		this.interrupt();
	}
}
