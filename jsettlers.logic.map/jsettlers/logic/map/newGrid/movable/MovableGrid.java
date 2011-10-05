package jsettlers.logic.map.newGrid.movable;

import jsettlers.common.movable.IMovable;

/**
 * This grid stores the position of the {@link IMovable}s.
 * 
 * @author Andreas Eberle
 * 
 */
public class MovableGrid {
	private final IMovable[][] movableGrid;

	public MovableGrid(short width, short height) {
		this.movableGrid = new IMovable[width][height];
	}

	public IMovable getMovableAt(short x, short y) {
		return this.movableGrid[x][y];
	}

	public void setMovable(short x, short y, IMovable movable) {
		this.movableGrid[x][y] = movable;
	}
}
