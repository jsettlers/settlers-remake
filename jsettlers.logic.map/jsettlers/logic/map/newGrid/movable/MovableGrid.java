package jsettlers.logic.map.newGrid.movable;

import java.io.Serializable;

import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.newGrid.interfaces.IHexMovable;

/**
 * This grid stores the position of the {@link IMovable}s.
 * 
 * @author Andreas Eberle
 * 
 */
public class MovableGrid implements Serializable {
	private static final long serialVersionUID = 7003522358013103962L;

	private final IHexMovable[][] movableGrid;

	public MovableGrid(short width, short height) {
		this.movableGrid = new IHexMovable[width][height];
	}

	public final IHexMovable getMovableAt(short x, short y) {
		return this.movableGrid[x][y];
	}

	public final void setMovable(short x, short y, IHexMovable movable) {
		this.movableGrid[x][y] = movable;
	}

	public final void movableLeft(ISPosition2D position, IHexMovable movable) {
		if (this.movableGrid[position.getX()][position.getY()] == movable) {
			this.movableGrid[position.getX()][position.getY()] = null;
		}
	}

	public final void movableEntered(ISPosition2D position, IHexMovable movable) {
		this.movableGrid[position.getX()][position.getY()] = movable;
	}

}
