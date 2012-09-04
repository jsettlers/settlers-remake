package jsettlers.logic.algorithms.borders;

import java.util.List;
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
	private boolean canceled = false;
	private final LinkedBlockingQueue<ShortPoint2D> positionsQueue = new LinkedBlockingQueue<ShortPoint2D>();
	private Thread bordersThread;

	/**
	 * This constructor creates a new instance of {@link BordersThread} and automatically launches a thread for it called "bordersThread".
	 * 
	 * @param grid
	 *            the grid on that the {@link BordersThread} will be operating
	 */
	public BordersThread(IBordersThreadGrid grid) {
		this.grid = grid;
		this.bordersThread = new Thread(this);
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
		byte player = grid.getPlayerAt(position.getX(), position.getY());
		boolean isBorder = false;

		for (EDirection currDir : EDirection.values) {
			short currNeighborX = currDir.getNextTileX(position.getX());
			short currNeighborY = currDir.getNextTileY(position.getY());

			if (!grid.isInBounds(currNeighborX, currNeighborY)) {
				continue;
			}

			byte neighborPlayer = grid.getPlayerAt(currNeighborX, currNeighborY);
			boolean neighborIsBorder = false;

			if (neighborPlayer != player) {
				isBorder = true;
			}

			if (neighborPlayer >= 0) { // this position is occupied by a player

				for (EDirection currNeighborDir : EDirection.values) {
					short nextX = currNeighborDir.getNextTileX(currNeighborX);
					short nextY = currNeighborDir.getNextTileY(currNeighborY);

					if (grid.isInBounds(nextX, nextY) && grid.getPlayerAt(nextX, nextY) != neighborPlayer) {
						neighborIsBorder = true;
						break;
					}
				}
			} // else the position is not occupied -> don't display a border here

			grid.setBorderAt(currNeighborX, currNeighborY, neighborIsBorder);
		}

		grid.setBorderAt(position.getX(), position.getY(), isBorder && player >= 0);
	}

	public void checkPosition(ShortPoint2D position) {
		synchronized (positionsQueue) {
			this.positionsQueue.offer(position);
		}
	}

	public void checkPositions(List<ShortPoint2D> occupiedPositions) {
		synchronized (positionsQueue) {
			this.positionsQueue.addAll(occupiedPositions);
		}
	}

	public void cancel() {
		this.canceled = true;
		synchronized (positionsQueue) {
			positionsQueue.notifyAll();
		}
		bordersThread.interrupt();
	}

	public void start() {
		bordersThread.setName("bordersThread");
		bordersThread.setDaemon(true);
		bordersThread.start();
	}

}
