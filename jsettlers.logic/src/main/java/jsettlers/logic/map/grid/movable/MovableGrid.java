/*******************************************************************************
 * Copyright (c) 2015 - 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.map.grid.movable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.player.IPlayer;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.coordinates.CoordinateStream;
import jsettlers.common.utils.mutables.MutableBoolean;
import jsettlers.logic.SerializationUtils;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.grid.landscape.IWalkableGround;
import jsettlers.logic.movable.interfaces.IAttackable;
import jsettlers.logic.movable.interfaces.ILogicMovable;

/**
 * This grid stores the position of the {@link IMovable}s.
 * 
 * @author Andreas Eberle
 */
public final class MovableGrid implements Serializable {
	private static final long serialVersionUID = 7003522358013103962L;

	private transient ILogicMovable[] movableGrid;
	private final IWalkableGround ground;
	private final short width;

	private final short height;

	public MovableGrid(short width, short height, IWalkableGround ground) {
		this.width = width;
		this.height = height;
		this.ground = ground;
		this.movableGrid = new ILogicMovable[width * height];
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		SerializationUtils.writeSparseArray(oos, movableGrid);
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		movableGrid = SerializationUtils.readSparseArray(ois, ILogicMovable.class);
	}

	public final ILogicMovable getMovableAt(int x, int y) {
		return this.movableGrid[x + y * width];
	}

	public ILogicMovable[] getMovableArray() {
		return movableGrid;
	}

	public final void movableLeft(ShortPoint2D position, ILogicMovable movable) {
		int idx = position.x + position.y * width;
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
	 */
	public final void movableEntered(ShortPoint2D position, ILogicMovable movable) {
		final short x = position.x;
		final short y = position.y;

		this.movableGrid[x + y * width] = movable;
		if (movable != null && movable.getMovableType() == EMovableType.BEARER) {
			ground.walkOn(x, y);
		}
	}

	/**
	 * 
	 * @param movable
	 *            The movable that needs to inform the others.
	 * @param x
	 *            x coordinate of the movables position.
	 * @param y
	 *            y coordinate of the movables position.
	 * @param informFullArea
	 *            If true, the full soldier update area is informed if the given movable is attackable.<br>
	 *            If false, only a circle is informed if the given movable is attackable.
	 */
	public void informMovables(ILogicMovable movable, short x, short y, boolean informFullArea) {
		// inform all movables of the given movable
		CoordinateStream area;
		if (informFullArea) {
			area = HexGridArea.stream(x, y, (short) 1, Constants.SOLDIER_SEARCH_RADIUS);
		} else {
			area = HexGridArea.streamBorder(x, y, Constants.SOLDIER_SEARCH_RADIUS - 1);
		}

		MutableBoolean foundOne = new MutableBoolean();
		IPlayer movablePlayer = movable.getPlayer();

		area.filterBounds(width, height).forEach((currX, currY) -> {
			ILogicMovable currMovable = getMovableAt(currX, currY);
			if (currMovable != null && isEnemy(movablePlayer, currMovable)) {
				currMovable.informAboutAttackable(movable);

				if (!foundOne.value) { // the first found movable is the one closest to the given movable.
					movable.informAboutAttackable(currMovable);
					foundOne.value = true;
				}
			}
		});
	}

	/**
	 * 
	 * @param player
	 *            The player id of the first player.
	 * @param otherAttackable
	 *            The other attackable. (Must not be null!)
	 * 
	 * @return
	 */
	public static boolean isEnemy(IPlayer player, IAttackable otherAttackable) {
		return otherAttackable.getPlayer().getTeamId() != player.getTeamId() && otherAttackable.isAttackable();
	}

	public boolean hasNoMovableAt(int x, int y) {
		return getMovableAt(x, y) == null;
	}
}
