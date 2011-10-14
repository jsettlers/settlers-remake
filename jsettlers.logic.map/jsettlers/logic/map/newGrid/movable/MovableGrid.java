package jsettlers.logic.map.newGrid.movable;

import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.hex.interfaces.IHexMovable;

/**
 * This grid stores the position of the {@link IMovable}s.
 * 
 * @author Andreas Eberle
 * 
 */
public class MovableGrid {
	private final IHexMovable[][] movableGrid;

	public MovableGrid(short width, short height) {
		this.movableGrid = new IHexMovable[width][height];
	}

	public IHexMovable getMovableAt(short x, short y) {
		return this.movableGrid[x][y];
	}

	public void setMovable(short x, short y, IHexMovable movable) {
		this.movableGrid[x][y] = movable;
	}

	public void movableLeft(ISPosition2D position, IHexMovable movable) {
		if (this.movableGrid[position.getX()][position.getY()] == movable) {
			this.movableGrid[position.getX()][position.getY()] = null;
		}
	}

	public void movableEntered(ISPosition2D position, IHexMovable movable) {
		this.movableGrid[position.getX()][position.getY()] = movable;
	}
}
