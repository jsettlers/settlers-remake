package jsettlers.logic.algorithms.path.astar;

import java.util.BitSet;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.AlgorithmConstants;
import jsettlers.logic.algorithms.heap.MinHeap;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.InvalidStartPositionException;
import jsettlers.logic.algorithms.path.Path;

/**
 * AStar algorithm to find paths from A to B on a hex grid
 * 
 * @author Andreas Eberle
 * 
 */
public final class HexAStar {
	private final AStarNode[][] nodes;
	private final IAStarPathMap map;
	private final BitSet openList;
	private final BitSet closedList;
	private final short height, width;

	private MinHeap<AStarNode> open = new MinHeap<AStarNode>(AlgorithmConstants.MINHEAP_INIT_NUMBER_OF_ELEMENTS);

	private final byte[] xDeltaArray;
	private final byte[] yDeltaArray;

	public HexAStar(IAStarPathMap map, short width, short height) {
		this.map = map;
		this.width = width;
		this.height = height;

		nodes = new AStarNode[height][width];
		openList = new BitSet(width * height);
		closedList = new BitSet(width * height);

		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				nodes[y][x] = new AStarNode(x, y);
			}
		}

		xDeltaArray = EDirection.getXDeltaArray();
		yDeltaArray = EDirection.getYDeltaArray();
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

		closedList.clear();
		openList.clear();

		open.clear();
		boolean found = false;
		initStartNode(sx, sy, tx, ty);

		while (!open.isEmpty()) {
			AStarNode currNode = open.deleteMin();

			short x = currNode.x;
			short y = currNode.y;

			setClosed(x, y);

			if (currNode.equals(tx, ty)) {
				found = true;
				break;
			}

			for (int i = 0; i < EDirection.NUMBER_OF_DIRECTIONS; i++) {
				short neighborX = (short) (x + xDeltaArray[i]);
				short neighborY = (short) (y + yDeltaArray[i]);

				if (isValidPosition(requester, neighborX, neighborY, blockedAtStart)) {
					AStarNode neighbor = nodes[neighborY][neighborX];
					int flatNeighborIdx = getFlatIdx(neighborX, neighborY);

					if (!closedList.get(flatNeighborIdx)) {
						float newCosts = currNode.cost + map.getCost(currNode.x, currNode.y, neighbor.x, neighbor.y);
						if (openList.get(flatNeighborIdx)) {
							if (neighbor.cost > newCosts) {
								neighbor.cost = newCosts;
								neighbor.depth = currNode.depth + 1;
								neighbor.parent = currNode;
								open.siftUp(neighbor);
							}
						} else {
							neighbor.cost = newCosts;
							neighbor.heuristic = map.getHeuristicCost(neighbor.x, neighbor.y, tx, ty);
							neighbor.parent = currNode;
							openList.set(flatNeighborIdx);
							neighbor.depth = currNode.depth + 1;
							open.insert(neighbor);

							map.markAsOpen(neighbor.x, neighbor.y);
						}
					}
				}
			}
		}

		if (found) {
			AStarNode curr = nodes[ty][tx];
			int pathlength = curr.depth;
			Path path = new Path(pathlength);

			int idx = pathlength;
			path.insertAt(idx, curr.getX(), curr.getY());

			while (curr.parent != null) {
				idx--;
				curr = curr.parent;
				path.insertAt(idx, curr.getX(), curr.getY());
			}

			return path;
		}

		return null;
	}

	private final void setClosed(short x, short y) {
		closedList.set(getFlatIdx(x, y));
		map.markAsClosed(x, y);
	}

	private final void initStartNode(short sx, short sy, short tx, short ty) {
		open.insert(nodes[sy][sx]);
		openList.set(getFlatIdx(sx, sy));
		nodes[sy][sx].depth = 0;
		nodes[sy][sx].parent = null;
		nodes[sy][sx].cost = 0;
		nodes[sy][sx].heuristic = map.getHeuristicCost(sx, sy, tx, ty);
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

	private final int getX(int flatIdx) {
		return flatIdx % width;
	}

	private final int getY(int flatIdx) {
		return flatIdx / width;
	}

}
