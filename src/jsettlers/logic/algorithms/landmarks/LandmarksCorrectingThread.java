package jsettlers.logic.algorithms.landmarks;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ISPosition2D;

/**
 * Thread to correct the landmarks. For example if Pioneers set all landmarks around a lake, this Thread will recognize it and take over the area of
 * the lake.
 * 
 * @author Andreas Eberle
 * 
 */
public class LandmarksCorrectingThread extends Thread {
	private final ILandmarksThreadMap map;
	private final ConcurrentLinkedQueue<ISPosition2D> queue = new ConcurrentLinkedQueue<ISPosition2D>();

	public LandmarksCorrectingThread(ILandmarksThreadMap map) {
		super("LandmarksCorrectingThread");
		this.map = map;

		this.start();
	}

	@Override
	public void run() {
		while (true) {
			if (queue.isEmpty()) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
			} else {
				ISPosition2D startPos = queue.poll();
				byte startPlayer = map.getPlayerAt(startPos);

				LinkedList<EDirection> allBlockedDirections = getBlockedDirection(startPos);
				for (EDirection startDirection : allBlockedDirections) {
					checkLandmarks(startPos, startPlayer, startDirection);
				}
			}
		}
	}

	private void checkLandmarks(ISPosition2D startPos, byte startPlayer, EDirection startDirection) {
		EDirection blockedDir = startDirection;

		ISPosition2D blocked = blockedDir.getNextHexPoint(startPos);
		ISPosition2D currBase = startPos;
		LinkedList<ISPosition2D> blockedBorder = new LinkedList<ISPosition2D>();
		blockedBorder.add(blocked);

		while (true) {
			EDirection neighborDir = blockedDir.getNeighbor(-1);
			ISPosition2D neighborPos = neighborDir.getNextHexPoint(currBase);

			if (map.isBlocked(neighborPos.getX(), neighborPos.getY())) {
				blocked = neighborPos;
				blockedDir = neighborDir;
				blockedBorder.add(blocked);
			} else if (map.getPlayerAt(neighborPos) == startPlayer) {
				currBase = neighborPos;
				blockedDir = EDirection.getDirection(currBase, blocked);

				if (neighborPos.equals(startPos)) {
					takeOverBlockedLand(blockedBorder, startPlayer);
					break;
				}
			} else {
				break;
			}
		}
	}

	private void takeOverBlockedLand(LinkedList<ISPosition2D> blockedBorder, byte startPlayer) {
		for (ISPosition2D curr : blockedBorder) {
			short y = curr.getY();
			for (short x = curr.getX();; x++) {
				if (map.isBlocked(x, y)) {
					map.setPlayerAt(x, y, startPlayer);
				} else {
					break;
				}
			}
		}
	}

	private LinkedList<EDirection> getBlockedDirection(ISPosition2D position) {
		LinkedList<EDirection> blockedDirections = new LinkedList<EDirection>();

		for (EDirection currDir : EDirection.values()) {
			short currX = currDir.getNextTileX(position.getX());
			short currY = currDir.getNextTileY(position.getY());
			if (map.isBlocked(currX, currY)) {
				blockedDirections.add(currDir);
			}
		}
		return blockedDirections;
	}

	public void addLandmarkedPosition(ISPosition2D pos) {
		queue.offer(pos);
	}
}
