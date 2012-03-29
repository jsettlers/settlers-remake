package jsettlers.logic.algorithms.path.astar;

import java.util.BitSet;

import jsettlers.common.Color;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
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

	@Override
	public final Path findPath(IPathCalculateable requester, ShortPoint2D target) {
		ShortPoint2D pos = requester.getPos();
		return findPath(requester, pos.getX(), pos.getY(), target.getX(), target.getY());
	}

	@Override
	public final Path findPath(IPathCalculateable requester, final short sx, final short sy, final short tx, final short ty) {
		final boolean blockedAtStart;
		if (!isInBounds(sx, sy)) {
			throw new InvalidStartPositionException("Start position is out of bounds!", sx, sy);
		} else if (!isInBounds(tx, ty) || isBlocked(requester, tx, ty)) {
			return null; // target can not be reached
		} else if (isBlocked(requester, sx, sy)) {
			// blockedAtStart = true;
			blockedAtStart = false;
		} else {
			blockedAtStart = false;
		}
		map.setDebugColor(tx, ty, Color.CYAN);

		final int targetFlatIdx = getFlatIdx(tx, ty);

		closedList.clear();
		openList.clear();

		open.clear();
		boolean found = false;
		initStartNode(sx, sy, tx, ty, requester, blockedAtStart);

		Point resultPoint = new Point();

		while (!open.isEmpty()) {
			int currFlatIdx = open.deleteMin();

			short x = getX(currFlatIdx);
			short y = getY(currFlatIdx);

			setClosed(x, y);

			if (targetFlatIdx == currFlatIdx) {
				found = true;
				break;
			}

			EDirection dir = getDirectionFromParentTo(currFlatIdx, x, y);

			if (dir.isHorizontal()) {
				calcHorizontalJumpPoint(x, y, tx, ty, dir, requester, blockedAtStart, resultPoint);
			} else {
				if (calcDiagonalJumpPoint(x, y, tx, ty, dir, requester, blockedAtStart, resultPoint)) {
					insertToOpen(x, y, resultPoint.x, resultPoint.y, tx, ty);
				}
			}
		}

		if (found) {
			int pathlength = depth[getFlatIdx(tx, ty)];
			Path path = new Path(pathlength);

			int idx = pathlength;
			int parentFlatIdx = targetFlatIdx;

			while (idx > 0) {
				idx--;
				path.insertAt(idx, getX(parentFlatIdx), getY(parentFlatIdx));
				parentFlatIdx = parent[parentFlatIdx];
			}
			path.initPath();
			return path;
		}

		return null;
	}

	private final void calcHorizontalJumpPoint(short x, short y, short tx, short ty, EDirection dir, IPathCalculateable requester,
			boolean blockedAtStart, Point result) {
		final EDirection leftNeighborDir = dir.getNeighbor(-1);
		final EDirection rightNeighborDir = dir.getNeighbor(1);

		short currX = x;
		short currY = y;

		boolean stopped = false;

		while (!stopped) {
			currX = dir.getNextTileX(currX);
			currY = dir.getNextTileY(currY);
			map.setDebugColor(currX, currY, Color.LIGHT_GREEN);

			if (!isValidPosition(requester, currX, currY, blockedAtStart)) { // check if the position is valid
				break;
			}

			if (currX == tx && currY == ty) { // check if this is the goal
				insertToOpen(x, y, currX, currY, tx, ty);
				break;
			}

			// no neighbors can be forced, so need to check that

			// check if the diagonal move finds a jump point
			if (calcDiagonalJumpPoint(currX, currY, tx, ty, rightNeighborDir, requester, blockedAtStart, result)) {
				insertToOpen(x, y, currX, currY, tx, ty); // add from parent to curr
				insertToOpen(currX, currY, result.x, result.y, tx, ty); // add from curr to found diagonal jump point
				stopped = true;
			}

			if (calcDiagonalJumpPoint(currX, currY, tx, ty, leftNeighborDir, requester, blockedAtStart, result)) {
				insertToOpen(x, y, currX, currY, tx, ty); // add from parent to curr
				insertToOpen(currX, currY, result.x, result.y, tx, ty); // add from curr to found diagonal jump point
				stopped = true;
			}
		}
	}

	private final boolean calcDiagonalJumpPoint(short x, short y, short tx, short ty, EDirection dir, IPathCalculateable requester,
			boolean blockedAtStart, Point result) {
		final EDirection leftNeighborDir = dir.getNeighbor(-1);
		final EDirection left2NeighborDir = dir.getNeighbor(-2);
		final EDirection rightNeighborDir = dir.getNeighbor(1);
		final EDirection right2NeighborDir = dir.getNeighbor(2);

		short currX = x;
		short currY = y;

		while (true) {
			currX = dir.getNextTileX(currX);
			currY = dir.getNextTileY(currY);
			map.setDebugColor(currX, currY, Color.GREEN);

			if (!isValidPosition(requester, currX, currY, blockedAtStart)) {// check if the position is valid
				return false;
			}

			if (currX == tx && currY == ty) { // check if this is the goal
				result.x = currX;
				result.y = currY;
				return true;
			}

			// check if this position has a forced neighbor
			if (isBlocked(requester, left2NeighborDir.getNextTileX(currX), left2NeighborDir.getNextTileY(currY))
					&& !isBlocked(requester, leftNeighborDir.getNextTileX(currX), leftNeighborDir.getNextTileY(currY))) {
				result.x = currX;
				result.y = currY;
				return true;
			}

			if (isBlocked(requester, right2NeighborDir.getNextTileX(currX), right2NeighborDir.getNextTileY(currY))
					&& !isBlocked(requester, rightNeighborDir.getNextTileX(currX), rightNeighborDir.getNextTileY(currY))) {
				result.x = currX;
				result.y = currY;
				return true;
			}

		}
	}

	private final void insertToOpen(short parentX, short parentY, short newX, short newY, final short tx, final short ty) {
		int flatJmPIdx = getFlatIdx(newX, newY);
		if (!openList.get(flatJmPIdx)) {
			int parentFlatIdx = getFlatIdx(parentX, parentY);

			// TODO calculate correct costs
			costsAndHeuristics[getCostsIdx(flatJmPIdx)] = costsAndHeuristics[getCostsIdx(parentFlatIdx)]
					+ Math.abs(Math.max(newX - parentX, newY - parentY));

			costsAndHeuristics[getHeuristicIdx(flatJmPIdx)] = getHeuristicCost(newX, newY, tx, ty);
			depth[flatJmPIdx] = depth[parentFlatIdx] + 1;
			parent[flatJmPIdx] = parentFlatIdx;
			openList.set(flatJmPIdx);
			open.insert(flatJmPIdx);

			map.markAsOpen(newX, newY);
		}
	}

	private EDirection getDirectionFromParentTo(int flatIdx, short x, short y) {
		final int parentFlatIdx = parent[flatIdx];
		final short parentX = getX(parentFlatIdx);
		final short parentY = getY(parentFlatIdx);

		return EDirection.getDirectionOfMultipleSteps(x - parentX, y - parentY);
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

	private final void initStartNode(short sx, short sy, short tx, short ty, IPathCalculateable requester, boolean blockedAtStart) {
		int flatIdx = getFlatIdx(sx, sy);
		closedList.set(flatIdx);
		map.markAsClosed(sx, sy);
		depth[flatIdx] = 0;
		parent[flatIdx] = -1;
		costsAndHeuristics[getCostsIdx(flatIdx)] = 0;
		costsAndHeuristics[getHeuristicIdx(flatIdx)] = getHeuristicCost(sx, sy, tx, ty);

		for (EDirection curr : EDirection.values) { // insert the neighbors of the start position
			short currX = curr.getNextTileX(sx);
			short currY = curr.getNextTileY(sy);

			if (isValidPosition(requester, currX, currY, blockedAtStart)) {
				insertToOpen(sx, sy, currX, currY, tx, ty);
			}
		}
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

		return (dx + dy);
	}

	private static final class Point {
		short x;
		short y;
	}
}
