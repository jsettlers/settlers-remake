package jsettlers.algorithms.supergrid;

import java.util.BitSet;
import java.util.HashSet;
import java.util.List;

import jsettlers.algorithms.supergrid.ISuperGridAStarGrid.IBlockedChangedListener;
import jsettlers.common.Color;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.astar.AStarMinHeap;
import jsettlers.logic.algorithms.path.astar.IAStarHeapable;

/**
 * This AStar works on a grid that has just one way point for {@value #STEP_WIDTH} steps on the given grid.<br>
 * Therefore the path search isn't fully optimal, but a good approximation.
 * 
 * @author Andreas Eberle
 * 
 */
public final class SuperGridAStar implements IAStarHeapable, IBlockedChangedListener {
	static final int STEP_WIDTH = 16;
	static final int NUMBER_OF_DIRECTIONS = 6;
	private static final byte[] DIR_DELTA_X = EDirection.getXDeltaArray();
	private static final byte[] DIR_DELTA_Y = EDirection.getYDeltaArray();

	private final ISuperGridAStarGrid grid;
	private final int width;
	private final int height;
	private final int numberOfPositions;

	private final float[] superGrid;
	private final float[] costsAndHeuristics;
	private final int[] parentHeapIdx;
	private final BitSet openClosed;

	private final AStarMinHeap open = new AStarMinHeap(this, 200);

	private final DijkstraForSupergrid costsDijkstra;

	/**
	 * 
	 * @param tileWidth
	 *            width of map in tiles
	 * @param tileHeight
	 *            height of map in tiles
	 * @param grid
	 */
	public SuperGridAStar(short tileWidth, short tileHeight, ISuperGridAStarGrid grid) {
		this.grid = grid;
		this.width = tileWidth / STEP_WIDTH + 1;
		this.height = tileHeight / STEP_WIDTH + 1;
		this.numberOfPositions = this.width * this.height;

		this.superGrid = new float[numberOfPositions * NUMBER_OF_DIRECTIONS];
		this.costsAndHeuristics = new float[numberOfPositions * 2];
		this.parentHeapIdx = new int[numberOfPositions * 2];
		this.openClosed = new BitSet(numberOfPositions * 2);

		this.costsDijkstra = new DijkstraForSupergrid(tileWidth, tileHeight, grid);

		precalculateSuperGrid();

		grid.setBlockedChangedListener(this);
	}

	public void findPath(short tileSx, short tileSy, short tileTx, short tileTy) {
		System.err.println("calculation AStar Path");
		openClosed.clear();
		open.clear();

		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				grid.setColor(x * STEP_WIDTH, y * STEP_WIDTH, Color.GREEN);
			}
		}

		int[] sNeighbors = getNeighbors(tileSx, tileSy);
		int[] tNeighbors = getNeighbors(tileTx, tileTy);

		int[] targets = new int[4];
		for (int i = 0; i < 4; i++) {
			int targetX = tNeighbors[2 * i];
			int targetY = tNeighbors[2 * i + 1];
			if (isInBounds(targetX, targetY)) {
				grid.setColor(targetX * STEP_WIDTH, targetY * STEP_WIDTH, Color.ORANGE);
				targets[i] = getIdx(targetX, targetY);
			}
		}

		for (int i = 0; i < 4; i++) {
			int neighborX = sNeighbors[2 * i];
			int neighborY = sNeighbors[2 * i + 1];
			if (isInBounds(neighborX, neighborY)) {
				grid.setColor(neighborX * STEP_WIDTH, neighborY * STEP_WIDTH, Color.ORANGE);
				int idx = getIdx(neighborX, neighborY);
				open.insert(idx);
				int doubleIdx = 2 * idx;
				openClosed.set(doubleIdx);
				costsAndHeuristics[doubleIdx] = getHeuristicCost(idx, tileSx, tileSy);
				costsAndHeuristics[doubleIdx + 1] = getHeuristicCost(idx, tileTx, tileTy);
				parentHeapIdx[doubleIdx] = -1;
			}
		}

		int foundTarget = -1;
		MAIN_LOOP: while (!open.isEmpty()) {
			int currIdx = open.deleteMin();
			openClosed.set(2 * currIdx + 1);

			grid.setColor((currIdx % width) * STEP_WIDTH, (currIdx / width) * STEP_WIDTH, Color.BLUE);

			for (int i = 0; i < 4; i++) {
				if (currIdx == targets[i]) {
					foundTarget = currIdx;
					break MAIN_LOOP;
				}
			}

			int dirBaseIdx = NUMBER_OF_DIRECTIONS * currIdx;
			float baseCosts = costsAndHeuristics[2 * currIdx];
			int currX = currIdx % width;
			int currY = currIdx / width;
			for (int i = 0; i < NUMBER_OF_DIRECTIONS; i++) {
				int neighborX = currX + DIR_DELTA_X[i];
				int neighborY = currY + DIR_DELTA_Y[i];
				int neighborIdx = neighborX + neighborY * width;

				int twoTimesNeighborIdx = 2 * neighborIdx;

				if (!isInBounds(neighborX, neighborY))
					continue; // if this position is out of bounds, continue

				float transitCosts = superGrid[dirBaseIdx + i];
				if (transitCosts == Float.MAX_VALUE) {
					continue; // if the path is blocked.
				}

				if (openClosed.get(twoTimesNeighborIdx + 1)) {
					continue; // if closed
				} else if (openClosed.get(twoTimesNeighborIdx)) { // if open
					if (costsAndHeuristics[twoTimesNeighborIdx] <= baseCosts + transitCosts) {
						continue; // if the cost's we found earlier are better.
					} else {
						parentHeapIdx[twoTimesNeighborIdx] = currIdx; // set parent
						costsAndHeuristics[twoTimesNeighborIdx] = baseCosts + transitCosts; // set costs
						open.siftUp(neighborIdx);
					}
				} else {
					openClosed.set(twoTimesNeighborIdx); // set as open
					parentHeapIdx[twoTimesNeighborIdx] = currIdx; // set parent
					costsAndHeuristics[twoTimesNeighborIdx] = baseCosts + transitCosts; // set costs
					costsAndHeuristics[twoTimesNeighborIdx + 1] = getHeuristicCost(neighborIdx, tileTx, tileTy); // set heuristic
					open.insert(neighborIdx);
				}
			}
		}

		while (foundTarget >= 0) {
			int x = foundTarget % width;
			int y = foundTarget / height;
			grid.setColor(x * STEP_WIDTH, y * STEP_WIDTH, Color.RED);

			foundTarget = parentHeapIdx[2 * foundTarget];
		}
	}

	private final boolean isInBounds(int x, int y) {
		return 0 <= x && 0 <= y && x < width && y < height;
	}

	private final int[] getNeighbors(int tileSx, int tileSy) {
		int sx = tileSx / STEP_WIDTH;
		int sy = tileSy / STEP_WIDTH;

		return new int[] { sx, sy, sx + 1, sy + 1, sx + 1, sy, sx, sy + 1 };
	}

	private final int getIdx(int x, int y) {
		return (y * width + x);
	}

	private final float getHeuristicCost(final int startIdx, final short tx, final short ty) {
		final int sx = (startIdx % width) * STEP_WIDTH;
		final int sy = (startIdx / width) * STEP_WIDTH;

		final float dx = (short) Math.abs(sx - tx);
		final float dy = (short) Math.abs(sy - ty);

		return (dx + 1.01f * dy);
	}

	@Override
	public final int getHeapIdx(int elementID) {
		return this.parentHeapIdx[2 * elementID + 1];
	}

	@Override
	public final void setHeapIdx(int elementID, int idx) {
		this.parentHeapIdx[2 * elementID + 1] = idx;
	}

	@Override
	public final float getHeapRank(int parentElementID) {
		int idx = 2 * parentElementID;
		return costsAndHeuristics[idx] + costsAndHeuristics[idx + 1];
	}

	private final void precalculateSuperGrid() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				updateCostsOnPos(x, y);
			}
		}
	}

	private void updateCostsOnPos(int x, int y) {
		float[] costs = costsDijkstra.calculateCostsFor(x, y);
		int idx = getIdx(x, y) * NUMBER_OF_DIRECTIONS;
		for (int i = 0; i < NUMBER_OF_DIRECTIONS; i++) {
			float cost = costs[i];
			superGrid[idx + i] = cost;
		}
	}

	@Override
	public void blockedChanged(List<ShortPoint2D> positions) {
		HashSet<ShortPoint2D> neighbors = new HashSet<ShortPoint2D>();

		for (ShortPoint2D currPos : positions) {
			int[] currNeighbors = getNeighbors(currPos.getX(), currPos.getY());
			for (int i = 0; i < 8;) {
				neighbors.add(new ShortPoint2D(currNeighbors[i++], currNeighbors[i++]));
			}
		}

		for (ShortPoint2D currNeighbor : neighbors) {
			short neighborX = currNeighbor.getX();
			short neighborY = currNeighbor.getY();

			if (isInBounds(neighborX, neighborY))
				updateCostsOnPos(neighborX, neighborY);
		}
	}

	@Override
	public void blockedChanged(int x, int y) {
		int[] neighbors = getNeighbors(x, y);
		for (int i = 0; i < 8;) {
			int neighborX = neighbors[i++];
			int neighborY = neighbors[i++];

			if (isInBounds(neighborX, neighborY))
				updateCostsOnPos(neighborX, neighborY);
		}
	}
}
