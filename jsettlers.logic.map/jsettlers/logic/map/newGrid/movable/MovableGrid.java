package jsettlers.logic.map.newGrid.movable;

import java.io.Serializable;

import jsettlers.common.map.shapes.HexBorderArea;
import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.landscape.IWalkableGround;
import jsettlers.logic.newmovable.NewMovable;

/**
 * This grid stores the position of the {@link IMovable}s.
 * 
 * @author Andreas Eberle
 */
public final class MovableGrid implements Serializable {
	private static final long serialVersionUID = 7003522358013103962L;

	private final NewMovable[] movableGrid;
	private final IWalkableGround ground;
	private final short width;

	private final short height;

	public MovableGrid(short width, short height, IWalkableGround ground) {
		this.width = width;
		this.height = height;
		this.ground = ground;
		this.movableGrid = new NewMovable[width * height];
	}

	private final int getIdx(int x, int y) {
		return y * width + x;
	}

	public final NewMovable getMovableAt(short x, short y) {
		return this.movableGrid[getIdx(x, y)];
	}

	public final void setMovable(short x, short y, NewMovable movable) {
		this.movableGrid[getIdx(x, y)] = movable;
	}

	public final void movableLeft(ShortPoint2D position, NewMovable movable) {
		int idx = getIdx(position.getX(), position.getY());
		if (this.movableGrid[idx] == movable) {
			this.movableGrid[idx] = null;
		}
	}

	/**
	 * Lets the given movable enter the given position.
	 * 
	 * @param position
	 *            Position to be entered.
	 * @param movable
	 *            Movable that enters the position.
	 * @param informFullArea
	 *            If true, the full soldier update area is informed if the given movable is attackable.<br>
	 *            If false, only a circle is informed if the given movable is attackable.
	 */
	public final void movableEntered(ShortPoint2D position, NewMovable movable, boolean informFullArea) {
		short x = position.getX();
		short y = position.getY();

		int idx = getIdx(x, y);
		if (idx < 0) {
			System.out.println("index < 0");
		}

		this.movableGrid[idx] = movable;
		if (movable != null && movable.getMovableType() == EMovableType.BEARER) {
			ground.walkOn(x, y);
		}

		// inform all movables of the given movable
		if (movable.isAttackable()) {
			IMapArea area;
			if (informFullArea) {
				area = new HexGridArea(x, y, (short) 1, Constants.SOLDIER_SEARCH_RADIUS);
			} else {
				area = new HexBorderArea(x, y, (short) (Constants.SOLDIER_SEARCH_RADIUS - 1));
			}

			boolean foundOne = false;
			byte movablePlayer = movable.getPlayer();

			for (ShortPoint2D curr : area) {
				short currX = curr.getX();
				short currY = curr.getY();
				if (0 <= currX && currX < width && 0 <= currY && currY < height) {
					NewMovable currMovable = getMovableAt(currX, currY);
					if (currMovable != null && currMovable.getPlayer() != movablePlayer) {
						currMovable.informAboutAttackable(movable);

						if (!foundOne) { // the first found movable is the one closest to the given movable.
							movable.informAboutAttackable(currMovable);
							foundOne = true;
						}
					}
				}
			}
		}
	}

	public NewMovable getEnemyInSearchArea(IMovable movable) {
		ShortPoint2D pos = movable.getPos();
		HexGridArea area = new HexGridArea(pos.getX(), pos.getY(), (short) 1, Constants.SOLDIER_SEARCH_RADIUS);

		byte movablePlayer = movable.getPlayer();

		for (ShortPoint2D curr : area) {
			short x = curr.getX();
			short y = curr.getY();
			if (0 <= x && x < width && 0 <= y && y < height) {
				NewMovable currMovable = getMovableAt(x, y);
				if (currMovable != null && currMovable.getPlayer() != movablePlayer) {
					return currMovable;
				}
			}
		}

		return null;
	}

	public boolean hasNoMovableAt(short x, short y) {
		return getMovableAt(x, y) == null;
	}
}
