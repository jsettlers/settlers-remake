package jsettlers.logic.map.newGrid.blocked;

/**
 * Grid that's storing the blocked information for fast access.
 * 
 * @author Andreas Eberle
 * 
 */
public class BlockedGrid {
	boolean[][] blocked;

	public BlockedGrid(short width, short height) {
		this.blocked = new boolean[width][height];
	}

	public boolean isBlocked(short x, short y) {
		return this.blocked[x][y];
	}

	public void setBlocked(short x, short y, boolean blocked) {
		this.blocked[x][y] = blocked;
	}
}
