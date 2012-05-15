package jsettlers.algorithms.supergrid.wnd;

import java.util.BitSet;
import java.util.LinkedList;

import jsettlers.common.movable.EDirection;

class DijkstraForSupergrid {
	private static final int DIR_DELTAS[];
	private static final int SEARCH_AREA_LENGTH = 2 * SuperGridAStar.STEP_WIDTH + 1;
	private static final int SEARCH_AREA_SIZE = SEARCH_AREA_LENGTH * SEARCH_AREA_LENGTH;
	private static final int OFFSET = SEARCH_AREA_SIZE / 2;
	private static final int SUPER_NEIGHBORS[];

	static {
		DIR_DELTAS = new int[SuperGridAStar.NUMBER_OF_DIRECTIONS];
		SUPER_NEIGHBORS = new int[SuperGridAStar.NUMBER_OF_DIRECTIONS];
		for (int i = 0; i < SuperGridAStar.NUMBER_OF_DIRECTIONS; i++) {
			DIR_DELTAS[i] = EDirection.values[i].gridDeltaY * SEARCH_AREA_LENGTH + EDirection.values[i].gridDeltaX;
			SUPER_NEIGHBORS[i] = EDirection.values[i].gridDeltaY * SuperGridAStar.STEP_WIDTH * SEARCH_AREA_LENGTH + EDirection.values[i].gridDeltaX
					* SuperGridAStar.STEP_WIDTH + OFFSET;
		}
	}

	private final IDijkstraGrid grid;
	private final short width;
	private final int numberOfTiles;

	private final BitSet used = new BitSet(SEARCH_AREA_SIZE);
	private final float depth[] = new float[SEARCH_AREA_SIZE];
	private final LinkedList<Integer> queue = new LinkedList<Integer>();

	DijkstraForSupergrid(short width, short height, IDijkstraGrid grid) {
		this.width = width;
		this.numberOfTiles = width * height;
		this.grid = grid;
	}

	public float[] calculateCostsFor(int sGridX, int sGridY) {
		if (grid.isBlocked(sGridX * SuperGridAStar.STEP_WIDTH, sGridY * SuperGridAStar.STEP_WIDTH)) { // if position is block, there can't be paths
			return new float[] { Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE };
		}

		used.clear();
		queue.clear();
		for (int dir = 0; dir < SuperGridAStar.NUMBER_OF_DIRECTIONS; dir++) {
			depth[SUPER_NEIGHBORS[dir]] = Float.MAX_VALUE;
		}

		depth[0 + OFFSET] = 0;
		used.set(0 + OFFSET);
		queue.offerLast(0);

		while (!queue.isEmpty()) {
			int curr = queue.pollFirst();

			for (int dir = 0; dir < SuperGridAStar.NUMBER_OF_DIRECTIONS; dir++) {
				int neighborDIdx = curr + DIR_DELTAS[dir]; // index in search area, where 0 is the center position.
				int neighborUsedIdx = neighborDIdx + OFFSET; // index in used and depth where 0 = offset

				if (!inSearchArea(neighborUsedIdx) || used.get(neighborUsedIdx)) // if pos is out of search area or has already been used.
					continue;

				int neighborDX = neighborDIdx % SEARCH_AREA_LENGTH;
				int neighborDY = neighborDIdx / SEARCH_AREA_LENGTH;
				int gridX = sGridX * SuperGridAStar.STEP_WIDTH + neighborDX;
				int gridY = sGridY * SuperGridAStar.STEP_WIDTH + neighborDY;

				if (outOfBounds(gridX, gridY)) // if position on original grid is out of bounds.
					continue;

				if (grid.isBlocked(gridX, gridY)) // if position is blocked on grid.
					continue;

				depth[neighborUsedIdx] = depth[curr + OFFSET] + 1; // TODO @Andreas exchange to costs on grid.
				used.set(neighborUsedIdx);
				queue.offerLast(neighborDIdx);
			}
		}

		float costs[] = new float[SuperGridAStar.NUMBER_OF_DIRECTIONS];
		for (int dir = 0; dir < SuperGridAStar.NUMBER_OF_DIRECTIONS; dir++) {
			costs[dir] = depth[SUPER_NEIGHBORS[dir]];
		}

		return costs;
	}

	private final boolean inSearchArea(int neighborIdx) {
		return 0 <= neighborIdx && neighborIdx < SEARCH_AREA_SIZE;
	}

	private final boolean outOfBounds(int tileX, int tileY) {
		int idx = tileY * width + tileX;
		return 0 > idx || idx >= numberOfTiles;
	}
}
