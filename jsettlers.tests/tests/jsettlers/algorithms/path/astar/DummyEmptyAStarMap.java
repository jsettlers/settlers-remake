package jsettlers.algorithms.path.astar;

import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.algorithms.path.astar.IAStarPathMap;
import jsettlers.common.Color;

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
	public boolean isBlocked(IPathCalculatable requester, int x, int y) {
		return blocked[x][y];
	}

	@Override
	public float getCost(int sx, int sy, int tx, int ty) {
		return 1;
	}

	@Override
	public void markAsOpen(int x, int y) {

	}

	@Override
	public void markAsClosed(int x, int y) {

	}

	public void setBlocked(int x, int y, boolean b) {
		blocked[x][y] = b;
	}

	@Override
	public void setDebugColor(int x, int y, Color color) {
	}

	@Override
	public short getBlockedPartition(int x, int y) {
		return 1;
	}
}
