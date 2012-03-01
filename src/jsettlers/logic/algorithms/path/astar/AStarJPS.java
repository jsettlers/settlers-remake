package jsettlers.logic.algorithms.path.astar;

import java.util.BitSet;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.AlgorithmConstants;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.InvalidStartPositionException;
import jsettlers.logic.algorithms.path.Path;

/**
 * AStar algorithm to find paths from A to B on a hex grid
 * 
 * @author Andreas Eberle
 * 
 */
public final class AStarJPS implements IAStar {
	private static final byte[] xDeltaArray = EDirection.getXDeltaArray();
	private static final byte[] yDeltaArray = EDirection.getYDeltaArray();

	private final IAStarPathMap map;

	private final short height;
	private final short width;

	private final BitSet openList;
	private final BitSet closedList;

	final float[] costsAndHeuristics;

	final int[] depth;
	final int[] parent;
	final int[] heapIdx;

	private final AStarMinHeap open;

	public AStarJPS(IAStarPathMap map, short width, short height) {
		this.map = map;
		this.width = width;
		this.height = height;
		this.open = new AStarMinHeap(this, AlgorithmConstants.MINHEAP_INIT_NUMBER_OF_ELEMENTS);

		this.openList = new BitSet(width * height);
		this.closedList = new BitSet(width * height);
		this.costsAndHeuristics = new float[width * height * 2];

		this.depth = new int[width * height];
		this.parent = new int[width * height];
		this.heapIdx = new int[width * height];
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

		final int targetFlatIdx = getFlatIdx(tx, ty);

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
						float newCosts = costsAndHeuristics[getCostsIdx(currFlatIdx)] + map.getCost(x, y, neighborX, neighborY);
						if (openList.get(flatNeighborIdx)) {
							if (costsAndHeuristics[getCostsIdx(flatNeighborIdx)] > newCosts) {
								costsAndHeuristics[getCostsIdx(flatNeighborIdx)] = newCosts;
								costsAndHeuristics[getHeuristicIdx(flatNeighborIdx)] = getHeuristicCost(neighborX, neighborY, tx, ty);
								depth[flatNeighborIdx] = depth[currFlatIdx] + 1;
								parent[flatNeighborIdx] = currFlatIdx;
								open.siftUp(flatNeighborIdx);
							}
						} else {
							costsAndHeuristics[getCostsIdx(flatNeighborIdx)] = newCosts;
							costsAndHeuristics[getHeuristicIdx(flatNeighborIdx)] = getHeuristicCost(neighborX, neighborY, tx, ty);
							depth[flatNeighborIdx] = depth[currFlatIdx] + 1;
							parent[flatNeighborIdx] = currFlatIdx;
							openList.set(flatNeighborIdx);
							open.insert(flatNeighborIdx);

							map.markAsOpen(neighborX, neighborY);
						}
					}
				}
			}
		}

		if (found) {
			int pathlength = depth[getFlatIdx(tx, ty)];
			Path path = new Path(pathlength);

			int idx = pathlength;
			int parentFlatIdx = targetFlatIdx;

			while (parentFlatIdx >= 0) {
				path.insertAt(idx, getX(parentFlatIdx), getY(parentFlatIdx));
				idx--;
				parentFlatIdx = parent[parentFlatIdx];
			}

			return path;
		}

		return null;
	}

	private static final int getHeuristicIdx(int flatIdx) {
		return flatIdx * 2 + 1;
	}

	private static final int getCostsIdx(int flatIdx) {
		return flatIdx * 2;
	}

	private final void setClosed(short x, short y) {
		closedList.set(getFlatIdx(x, y));
		map.markAsClosed(x, y);
	}

	private final void initStartNode(short sx, short sy, short tx, short ty) {
		int flatIdx = getFlatIdx(sx, sy);
		open.insert(flatIdx);
		openList.set(flatIdx);
		depth[flatIdx] = 0;
		parent[flatIdx] = -1;
		costsAndHeuristics[getCostsIdx(flatIdx)] = 0;
		costsAndHeuristics[getHeuristicIdx(flatIdx)] = getHeuristicCost(sx, sy, tx, ty);
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
	public final float getHeapRank(int identifier) {
		return costsAndHeuristics[getCostsIdx(identifier)] + costsAndHeuristics[getHeuristicIdx(identifier)];
	}

	@Override
	public final int getHeapIdx(int identifier) {
		return heapIdx[identifier];
	}

	@Override
	public final void setHeapIdx(int identifier, int idx) {
		heapIdx[identifier] = idx;
	}

	private static final float getHeuristicCost(final short sx, final short sy, final short tx, final short ty) {
		final float dx = (short) Math.abs(sx - tx);
		final float dy = (short) Math.abs(sy - ty);

		return (dx + 1.01f * dy);
	}
}
