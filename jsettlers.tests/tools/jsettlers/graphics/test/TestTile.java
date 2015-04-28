/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.graphics.test;

import java.awt.Color;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;

public class TestTile {
	private final short y;
	private final short x;
	private final byte height;

	private boolean river = false;

	private IMovable movable = null;
	private TestBuilding building = null;
	private IMapObject stack = null;
	private IMapObject object = null;
	private byte player = -1;

	public TestTile(short x, short y, byte height) {
		this.x = x;
		this.y = y;
		this.height = height;
	}

	public IBuilding getBuilding() {
		return this.building;
	}

	public byte getConstructionMark() {
		return 0;
	}

	public byte getHeight() {
		return this.height;
	}

	public ELandscapeType getLandscapeType() {
		if (isRiver()) {
			return ELandscapeType.RIVER1;
		} else if (this.height == 0) {
			return ELandscapeType.WATER1;
		} else if (this.height <= 3) {
			return ELandscapeType.SAND;
		} else if (this.height <= 10) {
			return ELandscapeType.GRASS;
		} else if (this.height <= 15) {
			return ELandscapeType.MOUNTAIN;
		} else {
			return ELandscapeType.SNOW;
		}
	}

	public IMapObject getHeadMapObject() {
		return this.object;
	}

	public IMovable getMovable() {
		return this.movable;
	}

	public IMapObject getStack() {
		return this.stack;
	}

	public byte getPlayer() {
		return this.player;
	}

	public void setMovable(IMovable moveavble) {
		this.movable = moveavble;
	}

	public boolean equals(TestTile other) {
		return other.x == this.x && other.y == this.y;
	}

	@Override
	public int hashCode() {
		return ShortPoint2D.hashCode(x, y);
	}

	public void setBuilding(TestBuilding building) {
		this.building = building;
	}

	public void setRiver(boolean river) {
		this.river = river;
	}

	public boolean isRiver() {
		return this.river;
	}

	public void setStack(IMapObject stack) {
		this.stack = stack;
	}

	public void setMapObject(IMapObject object) {
		this.object = object;
	}

	public void setPlayer(byte player) {
		this.player = player;
	}

	public Color getDebugColor() {
		return null;
	}

	public ShortPoint2D getPos() {
		return new ShortPoint2D(x, y);
	}

}
