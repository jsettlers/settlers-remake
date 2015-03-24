package jsettlers.algorithms.borders;

import java.util.concurrent.LinkedBlockingQueue;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;

/**
 * This thread calculates the positions that represent the border between the areas occupied by different players.
 * 
 * @author Andreas Eberle
 * 
 */
public class BordersThread implements Runnable {

	private final IBordersThreadGrid grid;
	private final LinkedBlockingQueue<ShortPoint2D> positionsQueue = new LinkedBlockingQueue<ShortPoint2D>();
	private final Thread bordersThread;

	private boolean canceled = false;

	/**
	 * This constructor creates a new instance of {@link BordersThread} and automatically launches a thread for it called "bordersThread".
	 * 
	 * @param grid
	 *            the grid on that the {@link BordersThread} will be operating
	 */
	public BordersThread(IBordersThreadGrid grid) {
		this.grid = grid;
		this.bordersThread = new Thread(this);
		this.bordersThread.setName("BordersThread");
		this.bordersThread.setDaemon(true);
	}

	@Override
	public void run() {
		while (!canceled) {
			ShortPoint2D position = null;
			while (position == null && !canceled) {
				try {
					position = positionsQueue.take();
				} catch (InterruptedException e) {
				}
			}
			if (!canceled) {
				calculateForPosition(position);
			}
		}
	}

	private void calculateForPosition(ShortPoint2D position) {
		short x = position.x;
		short y = position.y;
		byte player = grid.getPlayerIdAt(x, y);
		boolean isBorder = false;

		if (grid.getBlockedPartition(x, y) > 0) { // the position is not a blocked landscape
			for (EDirection currDir : EDirection.values) {
				short currNeighborX = currDir.getNextTileX(x);
				short currNeighborY = currDir.getNextTileY(y);

				if (!grid.isInBounds(currNeighborX, currNeighborY)) {
					continue;
				}

				byte neighborPlayer = grid.getPlayerIdAt(currNeighborX, currNeighborY);
				boolean neighborIsBorder = false;

				if (neighborPlayer != player && grid.getBlockedPartition(currNeighborX, currNeighborY) > 0) {
					isBorder = true;
				}

				if (neighborPlayer >= 0) { // this position is occupied by a player

					for (EDirection currNeighborDir : EDirection.values) {
						short nextX = currNeighborDir.getNextTileX(currNeighborX);
						short nextY = currNeighborDir.getNextTileY(currNeighborY);

						if (grid.isInBounds(nextX, nextY) && grid.getPlayerIdAt(nextX, nextY) != neighborPlayer
								&& grid.getBlockedPartition(nextX, nextY) > 0) {
							neighborIsBorder = true;
							break;
						}
					}
				} // else the position is not occupied -> don't display a border here

				grid.setBorderAt(currNeighborX, currNeighborY, neighborIsBorder);
			}
		}

		grid.setBorderAt(x, y, isBorder && player >= 0);
	}

	public void checkPosition(ShortPoint2D position) {
		this.positionsQueue.offer(position);
	}

	public void checkPositions(Iterable<ShortPoint2D> positions) {
		for (ShortPoint2D currPos : positions) {
			positionsQueue.offer(currPos);
		}
	}

	public void cancel() {
		this.canceled = true;
		bordersThread.interrupt();
	}

	public void start() {
		bordersThread.start();
	}
}
