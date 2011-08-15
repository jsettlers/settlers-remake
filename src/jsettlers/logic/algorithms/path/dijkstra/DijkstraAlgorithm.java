package jsettlers.logic.algorithms.path.dijkstra;

import java.util.LinkedList;

import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.wrapper.InvalidStartPositionException;

/**
 * this class implements a strict dijkstra algorithm
 * 
 * @author Andreas Eberle
 * 
 */
public class DijkstraAlgorithm implements IDijkstraAlgorithm {
	private static final int NO_LIST = -1;

	private final DijkstraNode[][] nodes;
	private final IDijkstraPathMap map;
	private int openList = 1;
	private int closedList = 2;
	private final short height, width;

	private LinkedList<DijkstraNode> currentList = new LinkedList<DijkstraNode>();
	private LinkedList<DijkstraNode> nextList = new LinkedList<DijkstraNode>();

	public DijkstraAlgorithm(IDijkstraPathMap map) {
		this.map = map;
		height = map.getHeight();
		width = map.getWidth();

		nodes = new DijkstraNode[height][width];

		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				nodes[y][x] = new DijkstraNode(x, y);
			}
		}
	}

	/**
	 * 
	 * @param requester
	 *            IReqeuster that requests this search
	 * @param cX
	 *            center x
	 * @param cY
	 *            center y
	 * @param searchRadius
	 *            radius to be searched
	 * @param type
	 *            type to be searched
	 * @return position of found thing of given type, or null if nothing could be found in the search radius.
	 */
	@Override
	public synchronized ISPosition2D find(IPathCalculateable requester, final short cX, final short cY, final short searchRadius,
			final ESearchType type) {
		if (!map.isInBounds(cX, cY)) {
			throw new InvalidStartPositionException(cX, cY);
		}

		if (closedList > Integer.MAX_VALUE - 10) {
			openList = 1;
			closedList = 2;
			resetListOfNodes();
		} else {
			openList += 2;
			closedList += 2;
		}

		currentList.clear();
		nextList.clear();

		initStartNode(cX, cY);

		short[][] neighbors = null;

		while (!nextList.isEmpty()) {
			currentList = nextList;
			nextList = new LinkedList<DijkstraNode>();

			for (DijkstraNode currNode : currentList) {
				currNode.inList = closedList;

				if (currNode.depth > searchRadius) {
					return null;
				}

				short x = currNode.x;
				short y = currNode.y;

				map.markAsClosed(x, y);

				if (map.fitsSearchType(x, y, type, requester)) {
					return new ShortPoint2D(x, y); // position found
				}

				neighbors = map.getNeighbors(x, y, neighbors);

				for (int i = 0; i < neighbors.length; i++) {
					short neighborX = neighbors[i][0];
					short neighborY = neighbors[i][1];

					if (map.isInBounds(neighborX, neighborY)) {
						DijkstraNode neighbor = nodes[neighborY][neighborX];
						if (neighbor.inList != closedList && neighbor.inList != openList) {
							neighbor.inList = openList;
							neighbor.depth = currNode.depth + 1;
							nextList.add(neighbor);

							map.markAsOpen(neighbor.x, neighbor.y);
						}
					}
				}
			}
		}
		return null;
	}

	private void initStartNode(short sx, short sy) {
		nextList.add(nodes[sy][sx]);
		nodes[sy][sx].inList = openList;
		nodes[sy][sx].depth = 0;
	}

	private void resetListOfNodes() {
		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				nodes[y][x].inList = NO_LIST;
			}
		}
	}

}
