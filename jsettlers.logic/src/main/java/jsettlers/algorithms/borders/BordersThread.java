/*******************************************************************************
 * Copyright (c) 2015 - 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.algorithms.borders;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * This thread calculates the positions that represent the border between the areas occupied by different players.
 * 
 * @author Andreas Eberle
 * 
 */
public class BordersThread implements Runnable {

	private final IBordersThreadGrid grid;
	private final LinkedBlockingQueue<ShortPoint2D> positionsQueue = new LinkedBlockingQueue<>();
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
			for (EDirection currDir : EDirection.VALUES) {
				int currNeighborX = currDir.getNextTileX(x);
				int currNeighborY = currDir.getNextTileY(y);

				if (!grid.isInBounds(currNeighborX, currNeighborY)) {
					continue;
				}
				if (grid.getBlockedPartition(currNeighborX, currNeighborY) <= 0) {
					continue; // this neighbor is in the sea => it can never be set.
				}

				byte neighborPlayer = grid.getPlayerIdAt(currNeighborX, currNeighborY);
				boolean neighborIsBorder = false;

				if (neighborPlayer != player) {
					isBorder = true;
				}

				if (neighborPlayer >= 0) { // this position is occupied by a player
					for (EDirection currNeighborDir : EDirection.VALUES) {
						int nextX = currNeighborDir.getNextTileX(currNeighborX);
						int nextY = currNeighborDir.getNextTileY(currNeighborY);

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

	public void checkArea(int x, int y, short width, short height) {
		int endX = x + width;
		int endY = y + height;

		for (; y < endY; y += 2) {
			for (int currX = x; currX < endX; currX += 2) {
				this.positionsQueue.offer(new ShortPoint2D(currX, y));
			}
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
