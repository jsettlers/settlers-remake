package jsettlers.logic.map.newGrid.blocked;

import java.io.Serializable;

/**
 * Grid that's storing the blocked information for fast access.
 * 
 * @author Andreas Eberle
 * 
 */
public class BlockedGrid implements Serializable {
	private static final long serialVersionUID = -413005884613149208L;

	private final boolean[][] blockedGrid;
	private final boolean[][] markedGrid;
	private final boolean[][] protectedGrid;

	public BlockedGrid(short width, short height) {
		this.blockedGrid = new boolean[width][height];
		this.markedGrid = new boolean[width][height];
		this.protectedGrid = new boolean[width][height];
	}

	public boolean isBlocked(short x, short y) {
		return this.blockedGrid[x][y];
	}

	public void setBlocked(short x, short y, boolean blocked) {
		this.blockedGrid[x][y] = blocked;
	}

	public boolean isMarked(short x, short y) {
		return this.markedGrid[x][y];
	}

	public void setMarked(short x, short y, boolean marked) {
		this.markedGrid[x][y] = marked;
	}

	public boolean isProtected(short x, short y) {
		return this.protectedGrid[x][y];
	}

	public void setProtected(short x, short y, boolean setProtected) {
		this.protectedGrid[x][y] = setProtected;
	}

}
