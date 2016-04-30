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
package jsettlers.logic.map.grid.objects;

import java.io.Serializable;
import java.util.Set;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.position.RelativePoint;

/**
 * extension to IMapObject to get functions needed by the hex grid.
 * 
 * @author Andreas Eberle
 */
public abstract class AbstractHexMapObject implements IMapObject, Serializable {
	private static final long serialVersionUID = 8466636267395026602L;

	private static final RelativePoint[] NO_BLOCKING = new RelativePoint[] {};
	private static final RelativePoint[] SELF_BLOCKING = new RelativePoint[] { new RelativePoint(0, 0) };
	/**
	 * next map object to build a list of map objects
	 */
	private transient AbstractHexMapObject next = null;

	public abstract boolean cutOff();

	/**
	 * is this map object blocking the position it has?
	 * 
	 * @return true if this map object is blocking it's position
	 */
	@Deprecated
	protected boolean isBlocking() {
		return false;
	}

	public RelativePoint[] getBlockedTiles() {
		if (isBlocking()) {
			return SELF_BLOCKING;
		} else {
			return NO_BLOCKING;
		}
	}

	@Override
	public final AbstractHexMapObject getNextObject() {
		return this.next;
	}

	/**
	 * appends the given object to this list.
	 * 
	 * @param mapObject
	 *            map object to be appended
	 */
	public final void addMapObject(AbstractHexMapObject mapObject) {
		if (this.next == null)
			this.next = mapObject;
		else
			this.next.addMapObject(mapObject);
	}

	/**
	 * The given mapObject is checked by ==, not by equals<br>
	 * NOTE: the first element can't be removed by this method.
	 * 
	 * @param mapObject
	 *            mapObject to be removed
	 * @return true if the mapObject had been found and removed<br>
	 *         false if it wasn't on the list
	 */
	public final boolean removeMapObject(AbstractHexMapObject mapObject) {
		if (this.next != null) {
			if (this.next == mapObject) {
				this.next = this.next.next;
				return true;
			} else {
				return this.next.removeMapObject(mapObject);
			}
		} else {
			return false;
		}
	}

	public void removeMapObjectTypes(Set<EMapObjectType> mapObjectTypes) {
		while (this.next != null && mapObjectTypes.contains(this.next.getObjectType())) {
			this.next = this.next.next;
		}

		if (this.next != null) {
			this.next.removeMapObjectTypes(mapObjectTypes);
		}
	}

	/**
	 * @return true if this map object can be cut.
	 */
	public abstract boolean canBeCut();

	/**
	 * @param mapObjectType
	 *            {@link EMapObjectType} to be checked
	 * @return true if any of the objects in this list is of the given mapObjectType and {@link #canBeCut()} returns true
	 */
	public boolean hasCuttableObject(EMapObjectType mapObjectType) {
		return this.getObjectType() == mapObjectType && this.canBeCut() || this.next != null && this.next.hasCuttableObject(mapObjectType);
	}

	/**
	 * @param mapObjectType
	 *            type to be looked for
	 * @return true if at least one of the map objects fits the given EMapObjectType
	 */
	public boolean hasMapObjectTypes(EMapObjectType... mapObjectTypes) {
		EMapObjectType mapObjectType = this.getObjectType();
		for (EMapObjectType curr : mapObjectTypes) {
			if (curr == mapObjectType) {
				return true;
			}
		}
		return this.next != null && this.next.hasMapObjectTypes(mapObjectTypes);
	}

	public AbstractHexMapObject getMapObject(EMapObjectType type) {
		if (this.getObjectType() == type) {
			return this;
		} else {
			return this.next != null ? this.next.getMapObject(type) : null;
		}
	}

	protected void handleRemove(int x, int y, MapObjectsManager mapObjectsManager, IMapObjectsManagerGrid grid) {
		setBlockedForObject(x, y, grid, false);
	}

	protected void handlePlacement(int x, int y, MapObjectsManager mapObjectsManager, IMapObjectsManagerGrid grid) {
		setBlockedForObject(x, y, grid, true);
	}

	private void setBlockedForObject(int oldX, int oldY, IMapObjectsManagerGrid grid, boolean blocked) {
		for (RelativePoint point : this.getBlockedTiles()) {
			int newX = point.calculateX(oldX);
			int newY = point.calculateY(oldY);
			if (grid.isInBounds(newX, newY)) {
				grid.setBlocked(newX, newY, blocked);
			}
		}
	}

}
