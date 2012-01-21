package jsettlers.logic.algorithms.borders;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ISPosition2D;

/**
 * This thread calculates the positions that represent the border between the areas occupied by different players.
 * 
 * @author Andreas Eberle
 * 
 */
public class BordersThread implements Runnable {

	private final IBordersThreadGrid grid;
	private boolean canceled = false;
	private final LinkedBlockingQueue<ISPosition2D> positionsQueue = new LinkedBlockingQueue<ISPosition2D>();

	/**
	 * This constructor creates a new instance of {@link BordersThread} and automatically launches a thread for it called "bordersThread".
	 * 
	 * @param grid
	 *            the grid on that the {@link BordersThread} will be operating
	 */
	public BordersThread(IBordersThreadGrid grid) {
		this.grid = grid;

		Thread thread = new Thread(this);
		thread.setName("bordersThread");
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		while (!canceled) {
			ISPosition2D position = null;
			while (position == null && !canceled) {
				try {
					position = positionsQueue.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			calculateForPosition(position);
		}
	}

	private void calculateForPosition(ISPosition2D position) {
		byte player = grid.getPlayer(position.getX(), position.getY());
		boolean isBorder = false;

		for (EDirection currDir : EDirection.values()) {
			short currNeighborX = currDir.getNextTileX(position.getX());
			short currNeighborY = currDir.getNextTileY(position.getY());

			if (!grid.isInBounds(currNeighborX, currNeighborY)) {
				continue;
			}

			byte neighborPlayer = grid.getPlayer(currNeighborX, currNeighborY);
			boolean neighborIsBorder = false;

			if (neighborPlayer != player) {
				isBorder = true;
			}

			if (neighborPlayer >= 0) { // this position is occupied by a player

				for (EDirection currNeighborDir : EDirection.valuesCached()) {
					short nextX = currNeighborDir.getNextTileX(currNeighborX);
					short nextY = currNeighborDir.getNextTileY(currNeighborY);

					if (grid.isInBounds(nextX, nextY) && grid.getPlayer(nextX, nextY) != neighborPlayer) {
						neighborIsBorder = true;
						break;
					}
				}
			} // else the position is not occupied -> don't display a border here

			grid.setBorder(currNeighborX, currNeighborY, neighborIsBorder);
		}

		grid.setBorder(position.getX(), position.getY(), isBorder && player >= 0);
	}

	public void checkPosition(ISPosition2D position) {
		synchronized (positionsQueue) {
			this.positionsQueue.offer(position);
		}
	}

	public void checkPositions(List<ISPosition2D> occupiedPositions) {
		synchronized (positionsQueue) {
			this.positionsQueue.addAll(occupiedPositions);
		}
	}

	public void cancel() {
		this.canceled = true;
		positionsQueue.notifyAll();
	}

}
