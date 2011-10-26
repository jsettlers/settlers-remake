package jsettlers.logic.algorithms.path.test;

import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.astar.IAStarPathMap;

public class DummyEmptyAStarMap implements IAStarPathMap {

	private final short width;
	private final short height;
	private final boolean[][] blocked;

	public DummyEmptyAStarMap(short width, short height) {
		this.width = width;
		this.height = height;
		this.blocked = new boolean[width][height];
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
		return blocked[x][y];
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

	public void setBlocked(int x, int y, boolean b) {
		blocked[x][y] = b;
	}
}
