package jsettlers.logic.algorithms.path.test;

import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.astar.IAStarPathMap;

/**
 * Dummy map for testing purposes of AStar.
 * 
 * @author Andreas Eberle
 * 
 */
public class DummyEmptyAStarMap implements IAStarPathMap {

	private final boolean[][] blocked;

	public DummyEmptyAStarMap(short width, short height) {
		this.blocked = new boolean[width][height];
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
	public float getHeuristicCost(short sx, short sy, short tx, short ty) {
		return 1;
	}

	public void setBlocked(int x, int y, boolean b) {
		blocked[x][y] = b;
	}
}
