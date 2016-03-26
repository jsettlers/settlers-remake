/*******************************************************************************
 * Copyright (c) 2015
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

	final float[] costs;

	final int[] depthParentHeap;

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
	public final Path findPath(IPathCalculatable requester, final ShortPoint2D startPos, final ShortPoint2D targetPos, AStarOptions opts) {
		if (!isInBounds(startPos.x, startPos.y)) {
			throw new InvalidStartPositionException("Start position is out of bounds!", startPos.x, startPos.y);
		} else if (!isInBounds(targetPos.x, targetPos.y) || isBlocked(requester, targetPos.x, targetPos.y) || map.getBlockedPartition(startPos.x, startPos.y) != map.getBlockedPartition(targetPos.x, targetPos.y)) {
			return null; // target can not be reached
		} else if (startPos.equals(targetPos)) {
			return null;
		} else if (isBlocked(requester, startPos.x, startPos.y)) {
			opts.partition = map.getBlockedPartition(startPos.x, startPos.y);
		}

		final int targetFlatIdx = getFlatIdx(targetPos.x, targetPos.y);

		closedBitSet.clear();
		openBitSet.clear();

		open.clear();
		boolean found = false;
		initStartNode(startPos.x, startPos.y, targetPos.x, targetPos.y);

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

				if (isValidPosition(requester, neighborX, neighborY, opts)) {
					final int flatNeighborIdx = getFlatIdx(neighborX, neighborY);

					if (!closedBitSet.get(flatNeighborIdx)) {
						final float newCosts = currPositionCosts + map.getCost(x, y, neighborX, neighborY);

						if (openBitSet.get(flatNeighborIdx)) {
							final float oldCosts = costs[flatNeighborIdx];

							if (oldCosts > newCosts) {
								costs[flatNeighborIdx] = newCosts;
								depthParentHeap[getDepthIdx(flatNeighborIdx)] = depthParentHeap[getDepthIdx(currFlatIdx)] + 1;
								depthParentHeap[getParentIdx(flatNeighborIdx)] = currFlatIdx;

								int heuristicCosts = getHeuristicCost(neighborX, neighborY, targetPos.x, targetPos.y);
								open.increasedPriority(flatNeighborIdx, oldCosts + heuristicCosts, newCosts + heuristicCosts);
							}

						} else {
							costs[flatNeighborIdx] = newCosts;
							depthParentHeap[getDepthIdx(flatNeighborIdx)] = depthParentHeap[getDepthIdx(currFlatIdx)] + 1;
							depthParentHeap[getParentIdx(flatNeighborIdx)] = currFlatIdx;
							openBitSet.set(flatNeighborIdx);
							open.insert(flatNeighborIdx, newCosts + getHeuristicCost(neighborX, neighborY, targetPos.x, targetPos.y));

							map.markAsOpen(neighborX, neighborY);
						}
					}
				}
			}
		}

		if (found) {
			int pathlength = depthParentHeap[getDepthIdx(getFlatIdx(targetPos.x, targetPos.y))];
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

	private static final int getDepthIdx(int flatIdx) {
		return 2 * flatIdx;
	}

	private static final int getParentIdx(int flatIdx) {
		return 2 * flatIdx + 1;
	}

	private final void setClosed(int x, int y) {
		closedBitSet.set(getFlatIdx(x, y));
		map.markAsClosed(x, y);
	}

	private final void initStartNode(int sx, int sy, int tx, int ty) {
		int flatIdx = getFlatIdx(sx, sy);
		depthParentHeap[getDepthIdx(flatIdx)] = 0;
		depthParentHeap[getParentIdx(flatIdx)] = -1;
		costs[flatIdx] = 0;

		open.insert(flatIdx, 0 + getHeuristicCost(sx, sy, tx, ty));
		openBitSet.set(flatIdx);
	}

	private final boolean isValidPosition(IPathCalculatable requester, int x, int y, AStarOptions opts) {
		return isInBounds(x, y) 
			&& ((opts.partition >= 0 && map.getBlockedPartition(x, y) == opts.partition) || !isBlocked(requester, x, y))
			&& (!opts.includeMovables || !map.isBlockedByMovable(requester, x, y));
	}

	private final boolean isInBounds(int x, int y) {
		return 0 <= x && x < width && 0 <= y && y < height;
	}

	private final boolean isBlocked(IPathCalculatable requester, int x, int y) {
		return map.isBlocked(requester, x, y);
	}

	private final int getFlatIdx(int x, int y) {
		return y * width + x;
	}

	private final int getX(int flatIdx) {
		return flatIdx % width;
	}

	private final int getY(int flatIdx) {
		return flatIdx / width;
	}

	private final int getHeuristicCost(final int sx, final int sy, final int tx, final int ty) {
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
