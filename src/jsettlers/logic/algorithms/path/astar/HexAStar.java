package jsettlers.logic.algorithms.path.astar;

import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.AlgorithmConstants;
import jsettlers.logic.algorithms.heap.MinHeap;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.algorithms.path.wrapper.InvalidStartPositionException;
import jsettlers.logic.algorithms.path.wrapper.requests.AbstractAStarRequest;

/**
 * AStar algorithm to find paths from A to B on a hex grid
 * 
 * @author Andreas Eberle
 * 
 */
public class HexAStar {
	private static final int NO_LIST = -1;

	private final AStarNode[][] nodes;
	private final IAStarPathMap map;
	private int openList = 1;
	private int closedList = 2;
	private final short height, width;

	private MinHeap<AStarNode> open = new MinHeap<AStarNode>(AlgorithmConstants.MINHEAP_INIT_NUMBER_OF_ELEMENTS);

	public HexAStar(IAStarPathMap map) {
		this.map = map;
		height = map.getHeight();
		width = map.getWidth();

		nodes = new AStarNode[height][width];

		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				nodes[y][x] = new AStarNode(x, y);
			}
		}
	}

	private short[][] neighbors;

	public Path findPath(AbstractAStarRequest request) {
		return findPath(request.getRequester(), request.getSx(), request.getSy(), request.getAStarTx(), request.getAStarTy());
	}

	public Path findPath(IPathCalculateable requester, final short sx, final short sy, final short tx, final short ty) {
		if (!map.isInBounds(sx, sy) || isBlocked(requester, sx, sy)) {
			throw new InvalidStartPositionException(sx, sy);
		} else if (!map.isInBounds(tx, ty) || isBlocked(requester, tx, ty)) {
			return null; // target can not be reached
		}

		if (closedList > Integer.MAX_VALUE - 10) {
			openList = 1;
			closedList = 2;
			resetListOfNodes();
		} else {
			openList += 2;
			closedList += 2;
		}

		open.clear();
		boolean found = false;
		initStartNode(sx, sy, tx, ty);

		while (!open.isEmpty()) {
			AStarNode currNode = open.deleteMin();
			currNode.inList = closedList;

			short x = currNode.x;
			short y = currNode.y;

			map.markAsClosed(x, y);

			if (currNode.equals(tx, ty)) {
				found = true;
				break;
			}

			neighbors = map.getNeighbors(x, y, neighbors);

			for (int i = 0; i < neighbors.length; i++) {
				short neighborX = neighbors[i][0];
				short neighborY = neighbors[i][1];

				if (isValidPosition(requester, neighborX, neighborY)) {
					AStarNode neighbor = nodes[neighborY][neighborX];
					if (neighbor.inList != closedList) {
						float newCosts = currNode.cost + map.getCost(currNode.x, currNode.y, neighbor.x, neighbor.y);
						if (neighbor.inList == openList) {
							if (neighbor.cost > newCosts) {
								neighbor.cost = newCosts;
								neighbor.depth = currNode.depth + 1;
								neighbor.parent = currNode;
								open.siftUp(neighbor);

								// System.out.println("heap check: " + open.doFullHeapCheck());
							}
						} else {
							neighbor.cost = newCosts;
							neighbor.heuristic = map.getHeuristicCost(neighbor.x, neighbor.y, tx, ty);
							neighbor.parent = currNode;
							neighbor.inList = openList;
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
			path.insertAt(idx, curr);

			while (curr.parent != null) {
				idx--;
				curr = curr.parent;
				path.insertAt(idx, curr);
			}

			return path;
		}

		return null;
	}

	private void initStartNode(short sx, short sy, short tx, short ty) {
		open.insert(nodes[sy][sx]);
		nodes[sy][sx].inList = openList;
		nodes[sy][sx].depth = 0;
		nodes[sy][sx].parent = null;
		nodes[sy][sx].cost = 0;
		nodes[sy][sx].heuristic = map.getHeuristicCost(sx, sy, tx, ty);
	}

	private final boolean isValidPosition(IPathCalculateable requester, short x, short y) {
		return map.isInBounds(x, y) && !isBlocked(requester, x, y);
	}

	private final boolean isBlocked(IPathCalculateable requester, short x, short y) {
		return map.isBlocked(requester, x, y);
	}

	private void resetListOfNodes() {
		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				nodes[y][x].inList = NO_LIST;
			}
		}
	}

	public Path findPath(IPathCalculateable requester, ISPosition2D target) {
		ISPosition2D pos = requester.getPos();
		return findPath(requester, pos.getX(), pos.getY(), target.getX(), target.getY());
	}
}
