package jsettlers.logic.algorithms.path.astar;

import java.util.BitSet;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.AlgorithmConstants;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.InvalidStartPositionException;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.algorithms.path.astar.heap.IHeapRankSupplier;
import jsettlers.logic.algorithms.path.astar.heap.MinHeap;

/**
 * AStar algorithm to find paths from A to B on a hex grid
 * 
 * @author Andreas Eberle
 * 
 */
public final class HexAStar implements IHeapRankSupplier {
	private static final byte[] xDeltaArray = EDirection.getXDeltaArray();
	private static final byte[] yDeltaArray = EDirection.getYDeltaArray();

	private final IAStarPathMap map;

	private final short height, width;
	private final BitSet openList;
	private final BitSet closedList;

	private final float[] costs;
	private final float[] heuristics;

	private final int[] depthParentHeap;

	private final MinHeap open;

	public HexAStar(IAStarPathMap map, short width, short height) {
		this.map = map;
		this.width = width;
		this.height = height;
		this.open = new MinHeap(this, AlgorithmConstants.MINHEAP_INIT_NUMBER_OF_ELEMENTS);

		openList = new BitSet(width * height);
		closedList = new BitSet(width * height);
		costs = new float[width * height];
		heuristics = new float[width * height];

		depthParentHeap = new int[width * height * 3];
	}

	public final Path findPath(IPathCalculateable requester, ISPosition2D target) {
		ISPosition2D pos = requester.getPos();
		return findPath(requester, pos.getX(), pos.getY(), target.getX(), target.getY());
	}

	public final Path findPath(IPathCalculateable requester, final short sx, final short sy, final short tx, final short ty) {
		final boolean blockedAtStart;
		if (!isInBounds(sx, sy)) {
			throw new InvalidStartPositionException("Start position is out of bounds!", sx, sy);
		} else if (!isInBounds(tx, ty) || isBlocked(requester, tx, ty)) {
			return null; // target can not be reached
		} else if (isBlocked(requester, sx, sy)) {
			blockedAtStart = true;
		} else {
			blockedAtStart = false;
		}

		int targetFlatIdx = getFlatIdx(tx, ty);

		closedList.clear();
		openList.clear();

		open.clear();
		boolean found = false;
		initStartNode(sx, sy, tx, ty);

		while (!open.isEmpty()) {
			int currFlatIdx = open.deleteMin();

			short x = getX(currFlatIdx);
			short y = getY(currFlatIdx);

			setClosed(x, y);

			if (targetFlatIdx == currFlatIdx) {
				found = true;
				break;
			}

			for (int i = 0; i < EDirection.NUMBER_OF_DIRECTIONS; i++) {
				short neighborX = (short) (x + xDeltaArray[i]);
				short neighborY = (short) (y + yDeltaArray[i]);

				if (isValidPosition(requester, neighborX, neighborY, blockedAtStart)) {
					int flatNeighborIdx = getFlatIdx(neighborX, neighborY);

					if (!closedList.get(flatNeighborIdx)) {
						float newCosts = costs[currFlatIdx] + map.getCost(x, y, neighborX, neighborY);
						if (openList.get(flatNeighborIdx)) {
							if (costs[flatNeighborIdx] > newCosts) {
								costs[flatNeighborIdx] = newCosts;
								heuristics[flatNeighborIdx] = map.getHeuristicCost(neighborX, neighborY, tx, ty);
								depthParentHeap[getDepthIdx(flatNeighborIdx)] = depthParentHeap[getDepthIdx(currFlatIdx)] + 1;
								depthParentHeap[getParentIdx(flatNeighborIdx)] = currFlatIdx;
								open.siftUp(flatNeighborIdx);
							}
						} else {
							costs[flatNeighborIdx] = newCosts;
							heuristics[flatNeighborIdx] = map.getHeuristicCost(neighborX, neighborY, tx, ty);
							depthParentHeap[getParentIdx(flatNeighborIdx)] = currFlatIdx;
							openList.set(flatNeighborIdx);
							depthParentHeap[getDepthIdx(flatNeighborIdx)] = depthParentHeap[getDepthIdx(currFlatIdx)] + 1;
							open.insert(flatNeighborIdx);

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

			while (parentFlatIdx >= 0) {
				path.insertAt(idx, getX(parentFlatIdx), getY(parentFlatIdx));
				idx--;
				parentFlatIdx = depthParentHeap[getParentIdx(parentFlatIdx)];
			}

			return path;
		}

		return null;
	}

	private static final int getDepthIdx(int flatIdx) {
		return 3 * flatIdx;
	}

	private static final int getParentIdx(int flatIdx) {
		return 3 * flatIdx + 1;
	}

	private static final int getHeapArrayIdx(int flatIdx) {
		return 3 * flatIdx + 2;
	}

	private final void setClosed(short x, short y) {
		closedList.set(getFlatIdx(x, y));
		map.markAsClosed(x, y);
	}

	private final void initStartNode(short sx, short sy, short tx, short ty) {
		int flatIdx = getFlatIdx(sx, sy);
		open.insert(flatIdx);
		openList.set(flatIdx);
		depthParentHeap[getDepthIdx(flatIdx)] = 0;
		depthParentHeap[getParentIdx(flatIdx)] = -1;
		costs[flatIdx] = 0;
		heuristics[flatIdx] = map.getHeuristicCost(sx, sy, tx, ty);
	}

	private final boolean isValidPosition(IPathCalculateable requester, short x, short y, boolean blockedAtStart) {
		return isInBounds(x, y) && (!isBlocked(requester, x, y) || blockedAtStart);
	}

	private final boolean isInBounds(short x, short y) {
		return 0 <= x && x < width && 0 <= y && y < height;
	}

	private final boolean isBlocked(IPathCalculateable requester, short x, short y) {
		return map.isBlocked(requester, x, y);
	}

	private final int getFlatIdx(short x, short y) {
		return y * width + x;
	}

	private final short getX(int flatIdx) {
		return (short) (flatIdx % width);
	}

	private final short getY(int flatIdx) {
		return (short) (flatIdx / width);
	}

	@Override
	public float getHeapRank(int identifier) {
		return costs[identifier] + heuristics[identifier];
	}

	@Override
	public int getHeapIdx(int identifier) {
		return depthParentHeap[getHeapArrayIdx(identifier)];
	}

	@Override
	public void setHeapIdx(int identifier, int idx) {
		depthParentHeap[getHeapArrayIdx(identifier)] = idx;
	}

}
