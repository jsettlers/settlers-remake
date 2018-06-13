/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
package jsettlers.algorithms.path.astar;

import java.util.BitSet;

import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.algorithms.path.InvalidStartPositionException;
import jsettlers.algorithms.path.Path;
import jsettlers.algorithms.path.astar.queues.bucket.AbstractBucketQueue;
import jsettlers.algorithms.path.astar.queues.bucket.ListMinBucketQueue;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;

/**
 * AStar algorithm to find paths from A to B on a hex grid
 *
 * @author Andreas Eberle
 *
 */
public final class BucketQueueAStar extends AbstractAStar {
	private static final byte[] xDeltaArray = EDirection.getXDeltaArray();
	private static final byte[] yDeltaArray = EDirection.getYDeltaArray();

	private final IAStarPathMap map;

	private final short height;
	private final short width;

	private final BitSet openBitSet;
	private final BitSet closedBitSet;

	private final float[] costs;
	private final int[]   depthParentHeap;

	private final AbstractBucketQueue open;

	public BucketQueueAStar(IAStarPathMap map, short width, short height) {
		this.map = map;
		this.width = width;
		this.height = height;

		this.open = new ListMinBucketQueue(width * height);

		this.openBitSet = new BitSet(width * height);
		this.closedBitSet = new BitSet(width * height);
		this.costs = new float[width * height];

		this.depthParentHeap = new int[width * height * 2];
	}

	@Override
	public final Path findPath(IPathCalculatable requester, ShortPoint2D target) {
		ShortPoint2D pos = requester.getPosition();
		return findPath(requester, pos.x, pos.y, target.x, target.y);
	}

	@Override
	public final Path findPath(IPathCalculatable requester, final short sx, final short sy, final short tx, final short ty) {
		final short blockedAtStartPartition;
		if (!isInBounds(sx, sy)) {
			throw new InvalidStartPositionException("Start position is out of bounds!", sx, sy);
		} else if (!isInBounds(tx, ty) || isBlocked(requester, tx, ty) || (!requester.isShip() && map.getBlockedPartition(sx, sy) != map.getBlockedPartition(tx, ty))) {
			return null; // target can not be reached
		} else if (sx == tx && sy == ty) {
			return null;
		} else if (isBlocked(requester, sx, sy)) {
			blockedAtStartPartition = map.getBlockedPartition(sx, sy);
		} else {
			blockedAtStartPartition = -1;
		}

		final int targetFlatIdx = getFlatIdx(tx, ty);

		closedBitSet.clear();
		openBitSet.clear();

		open.clear();
		boolean found = false;
		initStartNode(sx, sy, tx, ty);

		while (!open.isEmpty()) {
			int currFlatIdx = open.deleteMin();

			final int x = getX(currFlatIdx);
			final int y = getY(currFlatIdx);

			setClosed(x, y);

			if (targetFlatIdx == currFlatIdx) {
				found = true;
				break;
			}

			final float currPositionCosts = costs[currFlatIdx];

			for (int i = 0; i < EDirection.NUMBER_OF_DIRECTIONS; i++) {
				final int neighborX = x + xDeltaArray[i];
				final int neighborY = y + yDeltaArray[i];

				if (isValidPosition(requester, x, y, neighborX, neighborY, blockedAtStartPartition)) {
					final int flatNeighborIdx = getFlatIdx(neighborX, neighborY);

					if (!closedBitSet.get(flatNeighborIdx)) {
						final float newCosts = currPositionCosts + map.getCost(x, y, neighborX, neighborY);

						if (openBitSet.get(flatNeighborIdx)) {
							final float oldCosts = costs[flatNeighborIdx];

							if (oldCosts > newCosts) {
								costs[flatNeighborIdx] = newCosts;
								depthParentHeap[getDepthIdx(flatNeighborIdx)] = depthParentHeap[getDepthIdx(currFlatIdx)] + 1;
								depthParentHeap[getParentIdx(flatNeighborIdx)] = currFlatIdx;

								int heuristicCosts = getHeuristicCost(neighborX, neighborY, tx, ty);
								open.increasedPriority(flatNeighborIdx, oldCosts + heuristicCosts, newCosts + heuristicCosts);
							}

						} else {
							costs[flatNeighborIdx] = newCosts;
							depthParentHeap[getDepthIdx(flatNeighborIdx)] = depthParentHeap[getDepthIdx(currFlatIdx)] + 1;
							depthParentHeap[getParentIdx(flatNeighborIdx)] = currFlatIdx;
							openBitSet.set(flatNeighborIdx);
							open.insert(flatNeighborIdx, newCosts + getHeuristicCost(neighborX, neighborY, tx, ty));

							map.markAsOpen(neighborX, neighborY);
						}
					}
				}
			}
		}

		if (found) {
			int pathlength = depthParentHeap[getDepthIdx(getFlatIdx(tx, ty))];
			Path path = new Path(pathlength);

			int idx = pathlength;
			int parentFlatIdx = targetFlatIdx;

			while (idx > 0) {
				idx--;
				path.insertAt(idx, (short) getX(parentFlatIdx), (short) getY(parentFlatIdx));
				parentFlatIdx = depthParentHeap[getParentIdx(parentFlatIdx)];
			}

			return path;
		}

		return null;
	}

	private static int getDepthIdx(int flatIdx) {
		return 2 * flatIdx;
	}

	private static int getParentIdx(int flatIdx) {
		return 2 * flatIdx + 1;
	}

	private void setClosed(int x, int y) {
		closedBitSet.set(getFlatIdx(x, y));
		map.markAsClosed(x, y);
	}

	private void initStartNode(int sx, int sy, int tx, int ty) {
		int flatIdx = getFlatIdx(sx, sy);
		depthParentHeap[getDepthIdx(flatIdx)] = 0;
		depthParentHeap[getParentIdx(flatIdx)] = -1;
		costs[flatIdx] = 0;

		open.insert(flatIdx, getHeuristicCost(sx, sy, tx, ty));
		openBitSet.set(flatIdx);
	}

	private boolean isValidPosition(IPathCalculatable requester, int fromX, int fromY, int toX, int toY, short blockedAtStartPartition) {
		return isInBounds(toX, toY)
			&& (
			!isBlocked(requester, toX, toY)
				|| (
				blockedAtStartPartition >= 0 // if the start position was blocked, we can use blocked positions on the same island until
					&& map.getBlockedPartition(toX, toY) == blockedAtStartPartition // we leave the blocked area
					&& isBlocked(requester, fromX, fromY) // prevent reentering blocked positions when we left them already
			)
		);
	}

	private boolean isInBounds(int x, int y) {
		return 0 <= x && x < width && 0 <= y && y < height;
	}

	private boolean isBlocked(IPathCalculatable requester, int x, int y) {
		return map.isBlocked(requester, x, y);
	}

	private int getFlatIdx(int x, int y) {
		return y * width + x;
	}

	private int getX(int flatIdx) {
		return flatIdx % width;
	}

	private int getY(int flatIdx) {
		return flatIdx / width;
	}

	private int getHeuristicCost(final int sx, final int sy, final int tx, final int ty) {
		final int dx = (tx - sx);
		final int dy = (ty - sy);
		final int absDx = Math.abs(dx);
		final int absDy = Math.abs(dy);

		if (dx * dy > 0) { // dx and dy go in the same direction
			if (absDx > absDy) {
				return absDx;
			} else {
				return absDy;
			}
		} else {
			return absDx + absDy;
		}
	}
}
