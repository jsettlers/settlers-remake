package jsettlers.logic.map.newGrid.objects;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.hex.interfaces.AbstractHexMapObject;

/**
 * This grid stores the objects located at each position.
 * 
 * @author Andreas Eberle
 * 
 */
public class ObjectsGrid {
	private final AbstractHexMapObject[][] objectsGrid;

	public ObjectsGrid(short width, short height) {
		this.objectsGrid = new AbstractHexMapObject[width][height];
	}

	public AbstractHexMapObject getObjectsAt(short x, short y) {
		return objectsGrid[x][y];
	}

	public AbstractHexMapObject getMapObjectAt(short x, short y, EMapObjectType mapObjectType) {
		AbstractHexMapObject mapObjectHead = objectsGrid[x][y];

		return mapObjectHead != null ? mapObjectHead.getMapObject(mapObjectType) : null;
	}

	public AbstractHexMapObject removeMapObjectType(short x, short y, EMapObjectType mapObjectType) {
		AbstractHexMapObject mapObjectHead = objectsGrid[x][y];

		AbstractHexMapObject removed = null;
		if (mapObjectHead != null) {
			if (mapObjectHead.getObjectType() == mapObjectType) {
				removed = mapObjectHead;
				mapObjectHead = mapObjectHead.getNextObject();
			} else {
				removed = mapObjectHead.removeMapObjectType(mapObjectType);
			}
		}
		return removed;
	}

	public boolean removeMapObjectType(short x, short y, AbstractHexMapObject mapObject) {
		AbstractHexMapObject mapObjectHead = objectsGrid[x][y];
		if (mapObjectHead != null) {
			boolean removed;
			if (mapObjectHead == mapObject) {
				mapObjectHead = mapObjectHead.getNextObject();
				removed = true;
			} else {
				removed = mapObjectHead.removeMapObject(mapObject);
			}

			return removed;
		} else
			return false;
	}

	public void addMapObjectAt(short x, short y, AbstractHexMapObject mapObject) {
		AbstractHexMapObject mapObjectHead = objectsGrid[x][y];

		if (mapObjectHead == null) {
			mapObjectHead = mapObject;
		} else {
			mapObjectHead.addMapObject(mapObject);
		}
	}

	public boolean hasCuttableObject(short x, short y, EMapObjectType mapObjectType) {
		AbstractHexMapObject mapObjectHead = objectsGrid[x][y];

		return mapObjectHead != null && mapObjectHead.hasCuttableObject(mapObjectType);
	}

	public boolean hasMapObjectType(short x, short y, EMapObjectType mapObjectType) {
		AbstractHexMapObject mapObjectHead = objectsGrid[x][y];

		return mapObjectHead != null && mapObjectHead.hasMapObjectType(mapObjectType);
	}

	public boolean hasNeighborObjectType(short x, short y, EMapObjectType mapObjectType) {
		EDirection[] directions = EDirection.values();

		for (EDirection currDir : directions) {
			ISPosition2D currPos = currDir.getNextHexPoint(x, y);
			if (hasMapObjectType(currPos.getX(), currPos.getY(), mapObjectType)) {
				return true;
			}
		}
		return false;
	}

}
