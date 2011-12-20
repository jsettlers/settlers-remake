package jsettlers.logic.map.newGrid.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ISPosition2D;

/**
 * This grid stores the objects located at each position.
 * 
 * @author Andreas Eberle
 * 
 */
public class ObjectsGrid implements Serializable {
	private static final long serialVersionUID = 2919416226544282748L;

	private AbstractHexMapObject[][] objectsGrid;

	public ObjectsGrid(short width, short height) {
		this.objectsGrid = new AbstractHexMapObject[width][height];
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		int width = objectsGrid.length;
		int height = objectsGrid[0].length;
		oos.writeInt(width);
		oos.writeInt(height);

		for (short x = 0; x < width; x++) {
			for (short y = 0; y < height; y++) {
				AbstractHexMapObject currObject = getObjectsAt(x, y);

				while (currObject != null) {
					if (currObject.getObjectType() != EMapObjectType.WORKAREA_MARK) {
						oos.writeObject(currObject);
					}
					currObject = currObject.getNextObject();
				}
				oos.writeObject(null);
			}
		}
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		int width = ois.readInt();
		int height = ois.readInt();
		objectsGrid = new AbstractHexMapObject[width][height];

		for (short x = 0; x < width; x++) {
			for (short y = 0; y < height; y++) {
				AbstractHexMapObject currObject = (AbstractHexMapObject) ois.readObject();
				objectsGrid[x][y] = currObject;

				while (currObject != null) {
					AbstractHexMapObject newObject = (AbstractHexMapObject) ois.readObject();
					currObject.addMapObject(newObject);
					currObject = newObject;
				}
			}
		}
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
				objectsGrid[x][y] = mapObjectHead.getNextObject();
			} else {
				removed = mapObjectHead.removeMapObjectType(mapObjectType);
			}
		}
		return removed;
	}

	public boolean removeMapObject(short x, short y, AbstractHexMapObject mapObject) {
		AbstractHexMapObject mapObjectHead = objectsGrid[x][y];
		if (mapObjectHead != null) {
			boolean removed;
			if (mapObjectHead == mapObject) {
				objectsGrid[x][y] = mapObjectHead.getNextObject();
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
			objectsGrid[x][y] = mapObject;
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
