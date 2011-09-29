package jsettlers.logic.algorithms.path.test;

import jsettlers.common.movable.EDirection;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.astar.IAStarPathMap;

public class DummyEmptyPathfinderMap implements IAStarPathMap {

	private final short width;
	private final short height;

	public DummyEmptyPathfinderMap(short width, short height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public short getHeight() {
		return height;
	}

	@Override
	public short getWidth() {
		return width;
	}

	@Override
	public boolean isBlocked(IPathCalculateable requester, short x, short y) {
		return false;
	}

	@Override
	public short[][] getNeighbors(short x, short y, short[][] neighbors) {
		EDirection[] directions = EDirection.values();
		if (neighbors == null || neighbors.length != directions.length) {
			neighbors = new short[directions.length][2];
		}

		for (int i = 0; i < directions.length; i++) {
			neighbors[i][0] = directions[i].getNextTileX(x);
			neighbors[i][1] = directions[i].getNextTileY(y);
		}

		return neighbors;
	}

	@Override
	public float getCost(short sx, short sy, short tx, short ty) {
		return 1;
	}

	@Override
	public void markAsOpen(short x, short y) {

	}

	@Override
	public void markAsClosed(short x, short y) {

	}

	@Override
	public boolean isInBounds(short x, short y) {
		return 0 <= x && x < width && 0 <= y && y < height;
	}

	@Override
	public float getHeuristicCost(short sx, short sy, short tx, short ty) {
		return 1;
	}
}
