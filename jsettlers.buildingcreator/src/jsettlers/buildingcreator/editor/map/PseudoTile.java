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
package jsettlers.buildingcreator.editor.map;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;

public class PseudoTile {

	private final int x;
	private final int y;

	private IBuilding building;
	private int debugColor;
	private IMapObject stack;

	public PseudoTile(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public IBuilding getBuilding() {
		return building;
	}

	public int getDebugColor() {
		return debugColor;
	}

	public byte getHeight() {
		return 0;
	}

	public ELandscapeType getLandscapeType() {
		return ELandscapeType.DRY_GRASS;
	}

	public IMapObject getHeadMapObject() {
		return null;
	}

	public IMovable getMovable() {
		return null;
	}

	public IMapObject getStack() {
		return stack;
	}

	public byte getPlayer() {
		return 0;
	}

	public boolean equals(ShortPoint2D other) {
		return other.x == x && other.y == y;
	}

	@Override
	public int hashCode() {
		return ShortPoint2D.hashCode(x, y);
	};

	public void setBuilding(IBuilding building) {
		this.building = building;
	}

	public void setDebugColor(int debugColor) {
		this.debugColor = debugColor;
	}

	public void setStack(IMapObject stack) {
		this.stack = stack;
	}

	public ShortPoint2D getPos() {
		return new ShortPoint2D(x, y);
	}
}
