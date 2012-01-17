package jsettlers.logic.map.newGrid.movable;

import java.io.Serializable;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.newGrid.landscape.IWalkableGround;

/**
 * This grid stores the position of the {@link IMovable}s.
 * 
 * @author Andreas Eberle
 */
public final class MovableGrid implements Serializable {
	private static final long serialVersionUID = 7003522358013103962L;

	private final IHexMovable[][] movableGrid;

	private final IWalkableGround ground;

	public MovableGrid(short width, short height, IWalkableGround ground) {
		this.ground = ground;
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
		short x = position.getX();
		short y = position.getY();
		this.movableGrid[x][y] = movable;
		if (movable != null && movable.getMovableType() == EMovableType.BEARER) {
			ground.walkOn(x, y);
		}
	}

}
