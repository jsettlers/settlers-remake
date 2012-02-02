package jsettlers.logic.algorithms.landmarks;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ISPosition2D;

/**
 * Thread to correct the landmarks. For example if Pioneers set all landmarks around a lake, this Thread will recognize it and take over the area of
 * the lake.
 * 
 * @author Andreas Eberle
 * 
 */
public final class LandmarksCorrectingThread extends Thread {
	private final ILandmarksThreadGrid grid;
	private final LinkedBlockingQueue<ISPosition2D> queue = new LinkedBlockingQueue<ISPosition2D>();
	private boolean canceled;

	public LandmarksCorrectingThread(ILandmarksThreadGrid map) {
		super("LandmarksCorrectingThread");
		this.grid = map;

		this.setDaemon(true);
		this.start();
	}

	@Override
	public final void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		while (!canceled) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}

			ISPosition2D startPos = null;
			while (startPos == null) {
				try {
					startPos = queue.take();
				} catch (InterruptedException e) {
				}
			}
			checkLandmarks(startPos);
		}
	}

	private final void checkLandmarks(ISPosition2D startPos) {
		if (grid.isBlocked(startPos.getX(), startPos.getY()))
			return;

		short startPartition = grid.getPartitionAt(startPos.getX(), startPos.getY());

		LinkedList<EDirection> allBlockedDirections = getBlockedDirection(startPos);
		for (EDirection startDirection : allBlockedDirections) {
			checkLandmarks(startPos, startPartition, startDirection);
		}
	}

	private final void checkLandmarks(ISPosition2D startPos, short startPartition, EDirection startDirection) {
		EDirection blockedDir = startDirection;

		ISPosition2D blocked = blockedDir.getNextHexPoint(startPos);
		ISPosition2D currBase = startPos;
		LinkedList<ISPosition2D> blockedBorder = new LinkedList<ISPosition2D>();
		blockedBorder.add(blocked);

		for (byte i = 0; i < EDirection.NUMBER_OF_DIRECTIONS; i++) {
			EDirection neighborDir = blockedDir.getNeighbor(-1);
			ISPosition2D neighborPos = neighborDir.getNextHexPoint(currBase);

			if (!grid.isInBounds(neighborPos.getX(), neighborPos.getY())) {
				takeOverBlockedLand(blockedBorder, startPartition);
				break;
			} else if (grid.isBlocked(neighborPos.getX(), neighborPos.getY())) {
				blocked = neighborPos;
				blockedDir = neighborDir;
				blockedBorder.add(blocked);
				i = 0;
			} else if (grid.getPartitionAt(neighborPos.getX(), neighborPos.getY()) == startPartition) {
				currBase = neighborPos;
				blockedDir = EDirection.getDirection(currBase, blocked);
				i = 0;

				if (neighborPos.equals(startPos)) {
					takeOverBlockedLand(blockedBorder, startPartition);
					break;
				}
			} else {
				break;
			}
		}
	}

	private final void takeOverBlockedLand(LinkedList<ISPosition2D> blockedBorder, short startPartition) {
		for (ISPosition2D curr : blockedBorder) {
			short y = curr.getY();
			for (short x = curr.getX();; x++) {
				if (grid.isInBounds(x, y) && grid.isBlocked(x, y)) {
					grid.setPartitionAndPlayerAt(x, y, startPartition);
				} else {
					break;
				}
			}
		}
	}

	private final LinkedList<EDirection> getBlockedDirection(ISPosition2D position) {
		LinkedList<EDirection> blockedDirections = new LinkedList<EDirection>();

		for (EDirection currDir : EDirection.values) {
			short currX = currDir.getNextTileX(position.getX());
			short currY = currDir.getNextTileY(position.getY());
			if (grid.isInBounds(currX, currY) && grid.isBlocked(currX, currY)) {
				blockedDirections.add(currDir);
			}
		}
		return blockedDirections;
	}

	public final void addLandmarkedPosition(ISPosition2D pos) {
		queue.offer(pos);
	}

	public final void addLandmarkedPositions(List<ISPosition2D> occupiedPositions) {
		queue.addAll(occupiedPositions);
	}

	public void cancel() {
		canceled = true;
		this.interrupt();
	}
}
