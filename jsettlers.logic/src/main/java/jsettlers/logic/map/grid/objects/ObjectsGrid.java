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
package jsettlers.logic.map.grid.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;

import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.coordinates.CoordinateStream;
import jsettlers.logic.SerializationUtils;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.interfaces.IAttackable;
import jsettlers.logic.movable.interfaces.IInformable;

/**
 * This grid stores the objects located at each position.
 * 
 * @author Andreas Eberle
 * 
 */
public final class ObjectsGrid implements Serializable {
	private static final long serialVersionUID = 2919416226544282748L;

	private final short width;
	private final short height;

	private transient AbstractHexMapObject[] objectsGrid;
	private transient Building[] buildingsGrid;

	public ObjectsGrid(short width, short height) {
		this.width = width;
		this.height = height;
		this.objectsGrid = new AbstractHexMapObject[width * height];
		this.buildingsGrid = new Building[width * height];
	}

	private final void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();

		SerializationUtils.writeSparseArray(oos, buildingsGrid);

		int length = objectsGrid.length;
		oos.writeInt(length);

		for (int idx = 0; idx < length; idx++) {
			AbstractHexMapObject currObject = objectsGrid[idx];

			if (currObject != null) {
				oos.writeInt(idx);
				while (currObject != null) {
					if (currObject.getObjectType().persistent) {
						oos.writeObject(currObject);
					}
					currObject = currObject.getNextObject();
				}
				oos.writeObject(null);
			}
		}
		oos.writeInt(-1); // this is used to detect the end
	}

	private final void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();

		buildingsGrid = SerializationUtils.readSparseArray(ois, Building.class);

		int length = ois.readInt();
		objectsGrid = new AbstractHexMapObject[length];

		int index = ois.readInt();
		while (index >= 0) {
			AbstractHexMapObject currObject = (AbstractHexMapObject) ois.readObject();
			objectsGrid[index] = currObject;

			while (currObject != null) {
				AbstractHexMapObject newObject = (AbstractHexMapObject) ois.readObject();
				currObject.addMapObject(newObject);
				currObject = newObject;
			}

			index = ois.readInt();
		}
	}

	public final AbstractHexMapObject getObjectsAt(int x, int y) {
		return objectsGrid[x + y * width];
	}

	public final IMapObject[] getObjectArray() {
		return objectsGrid;
	}

	public final AbstractHexMapObject getMapObjectAt(int x, int y, EMapObjectType mapObjectType) {
		AbstractHexMapObject mapObjectHead = objectsGrid[x + y * width];

		return mapObjectHead != null ? mapObjectHead.getMapObject(mapObjectType) : null;
	}

	public final void removeMapObjectTypes(int x, int y, Set<EMapObjectType> mapObjectTypes) {
		final int idx = x + y * width;
		AbstractHexMapObject mapObjectHead = objectsGrid[idx];

		while (mapObjectHead != null && mapObjectTypes.contains(mapObjectHead.getObjectType())) {
			mapObjectHead = mapObjectHead.getNextObject();
			objectsGrid[idx] = mapObjectHead;
		}

		if (mapObjectHead != null) {
			mapObjectHead.removeMapObjectTypes(mapObjectTypes);
		}
	}

	public final boolean removeMapObject(int x, int y, AbstractHexMapObject mapObject) {
		final int idx = x + y * width;
		AbstractHexMapObject mapObjectHead = objectsGrid[idx];
		if (mapObjectHead != null) {
			boolean removed;
			if (mapObjectHead == mapObject) {
				objectsGrid[idx] = mapObjectHead.getNextObject();
				removed = true;
			} else {
				removed = mapObjectHead.removeMapObject(mapObject);
			}

			return removed;
		} else
			return false;
	}

	public final void addMapObjectAt(int x, int y, AbstractHexMapObject mapObject) {
		final int idx = x + y * width;

		AbstractHexMapObject mapObjectHead = objectsGrid[idx];

		if (mapObjectHead == null) {
			objectsGrid[idx] = mapObject;
		} else {
			mapObjectHead.addMapObject(mapObject);
		}
	}

	public final boolean hasCuttableObject(int x, int y, EMapObjectType mapObjectType) {
		AbstractHexMapObject mapObjectHead = objectsGrid[x + y * width];

		return mapObjectHead != null && mapObjectHead.hasCuttableObject(mapObjectType);
	}

	public final boolean hasMapObjectType(int x, int y, EMapObjectType... mapObjectTypes) {
		AbstractHexMapObject mapObjectHead = objectsGrid[x + y * width];

		return mapObjectHead != null && mapObjectHead.hasMapObjectTypes(mapObjectTypes);
	}

	public final boolean hasNeighborObjectType(int x, int y, EMapObjectType... mapObjectTypes) {
		EDirection[] directions = EDirection.VALUES;

		for (EDirection currDir : directions) {
			ShortPoint2D currPos = currDir.getNextHexPoint(x, y);
			if (hasMapObjectType(currPos.x, currPos.y, mapObjectTypes)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Informs towers of the attackable in their search radius..
	 * 
	 * @param position
	 *            The new position of the movable.
	 * @param attackable
	 *            The attackable that moved to the given position.
	 * @param informFullArea
	 *            if true, the full area is informed<br>
	 *            if false, only the border of the area is informed.
	 * @param informAttackable
	 */
	public void informObjectsAboutAttackable(ShortPoint2D position, IAttackable attackable, boolean informFullArea, boolean informAttackable) {
		CoordinateStream area;
		if (informFullArea) {
			area = HexGridArea.stream(position.x, position.y, 1, Constants.TOWER_ATTACKABLE_SEARCH_RADIUS);
		} else {
			area = HexGridArea.streamBorder(position.x, position.y, Constants.TOWER_ATTACKABLE_SEARCH_RADIUS - 1);
		}

		byte movableTeam = attackable.getPlayer().getTeamId();

		area.filterBounds(width, height)
				.forEach((x, y) -> {
					IAttackable currTower = (IAttackable) getMapObjectAt(x, y, EMapObjectType.ATTACKABLE_TOWER);

					if (currTower != null && currTower.getPlayer().getTeamId() != movableTeam) {
						currTower.informAboutAttackable(attackable);

						if (informAttackable) {
							attackable.informAboutAttackable(currTower);
						}
					}

					IInformable currInformable = (IInformable) getMapObjectAt(x, y, EMapObjectType.INFORMABLE_MAP_OBJECT);
					if (currInformable != null) {
						currInformable.informAboutAttackable(attackable);
					}
				});
	}

	public void setBuildingArea(FreeMapArea area, Building building) {
		for (ShortPoint2D curr : area) {
			buildingsGrid[curr.x + curr.y * width] = building;
		}
	}

	public Building getBuildingAt(int x, int y) {
		return buildingsGrid[x + y * width];
	}

	public boolean isBuildingAt(int x, int y) {
		return buildingsGrid[x + y * width] != null;
	}

}
