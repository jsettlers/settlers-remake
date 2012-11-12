package jsettlers.logic.map.newGrid.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.HexBorderArea;
import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.newmovable.interfaces.IAttackable;
import jsettlers.logic.newmovable.interfaces.IInformable;

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

	private transient AbstractHexMapObject[] objectsGrid; // don't use default serialization for this => transient
	private final Building[] buildingsGrid;

	public ObjectsGrid(short width, short height) {
		this.width = width;
		this.height = height;
		this.objectsGrid = new AbstractHexMapObject[width * height];
		this.buildingsGrid = new Building[width * height];
	}

	private final void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		int length = objectsGrid.length;
		oos.writeInt(length);

		for (int idx = 0; idx < length; idx++) {
			AbstractHexMapObject currObject = objectsGrid[idx];

			while (currObject != null) {
				if (currObject.getObjectType() != EMapObjectType.WORKAREA_MARK) {
					oos.writeObject(currObject);
				}
				currObject = currObject.getNextObject();
			}
			oos.writeObject(null);
		}
	}

	private final void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		int length = ois.readInt();
		objectsGrid = new AbstractHexMapObject[length];

		for (int idx = 0; idx < length; idx++) {
			AbstractHexMapObject currObject = (AbstractHexMapObject) ois.readObject();
			objectsGrid[idx] = currObject;

			while (currObject != null) {
				AbstractHexMapObject newObject = (AbstractHexMapObject) ois.readObject();
				currObject.addMapObject(newObject);
				currObject = newObject;
			}
		}
	}

	public final AbstractHexMapObject getObjectsAt(int x, int y) {
		return objectsGrid[x + y * width];
	}

	public final AbstractHexMapObject getMapObjectAt(int x, int y, EMapObjectType mapObjectType) {
		AbstractHexMapObject mapObjectHead = objectsGrid[x + y * width];

		return mapObjectHead != null ? mapObjectHead.getMapObject(mapObjectType) : null;
	}

	public final AbstractHexMapObject removeMapObjectType(short x, short y, EMapObjectType mapObjectType) {
		final int idx = x + y * width;
		AbstractHexMapObject mapObjectHead = objectsGrid[idx];

		AbstractHexMapObject removed = null;
		if (mapObjectHead != null) {
			if (mapObjectHead.getObjectType() == mapObjectType) {
				removed = mapObjectHead;
				objectsGrid[idx] = mapObjectHead.getNextObject();
			} else {
				removed = mapObjectHead.removeMapObjectType(mapObjectType);
			}
		}
		return removed;
	}

	public final boolean removeMapObject(short x, short y, AbstractHexMapObject mapObject) {
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

	public final void addMapObjectAt(short x, short y, AbstractHexMapObject mapObject) {
		final int idx = x + y * width;

		AbstractHexMapObject mapObjectHead = objectsGrid[idx];

		if (mapObjectHead == null) {
			objectsGrid[idx] = mapObject;
		} else {
			mapObjectHead.addMapObject(mapObject);
		}
	}

	public final boolean hasCuttableObject(short x, short y, EMapObjectType mapObjectType) {
		AbstractHexMapObject mapObjectHead = objectsGrid[x + y * width];

		return mapObjectHead != null && mapObjectHead.hasCuttableObject(mapObjectType);
	}

	public final boolean hasMapObjectType(short x, short y, EMapObjectType mapObjectType) {
		AbstractHexMapObject mapObjectHead = objectsGrid[x + y * width];

		return mapObjectHead != null && mapObjectHead.hasMapObjectType(mapObjectType);
	}

	public final boolean hasNeighborObjectType(short x, short y, EMapObjectType mapObjectType) {
		EDirection[] directions = EDirection.values;

		for (EDirection currDir : directions) {
			ShortPoint2D currPos = currDir.getNextHexPoint(x, y);
			if (hasMapObjectType(currPos.getX(), currPos.getY(), mapObjectType)) {
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
	 * @param b
	 */
	public void informObjectsAboutAttackble(ShortPoint2D position, IAttackable attackable, boolean informFullArea, boolean informAttackable) {
		IMapArea area;
		if (informFullArea) {
			area = new HexGridArea(position.getX(), position.getY(), (short) 1, Constants.TOWER_SEARCH_RADIUS);
		} else {
			area = new HexBorderArea(position.getX(), position.getY(), (short) (Constants.TOWER_SEARCH_RADIUS - 1));
		}

		byte movablePlayer = attackable.getPlayerId();

		for (ShortPoint2D curr : area) {
			short x = curr.getX();
			short y = curr.getY();
			if (0 <= x && x < width && 0 <= y && y < height) {
				IAttackable currTower = (IAttackable) getMapObjectAt(x, y, EMapObjectType.ATTACKABLE_TOWER);

				if (currTower != null && currTower.getPlayerId() != movablePlayer) {
					currTower.informAboutAttackable(attackable);

					if (informAttackable) {
						attackable.informAboutAttackable(currTower);
					}
				}

				IInformable currInformable = (IInformable) getMapObjectAt(x, y, EMapObjectType.INFORMABLE_MAP_OBJECT);
				if (currInformable != null) {
					currInformable.informAboutAttackable(attackable);
				}
			}
		}
	}

	public void setBuildingArea(FreeMapArea area, Building buildingValue) {
		for (ShortPoint2D curr : area) {
			buildingsGrid[curr.getX() + curr.getY() * width] = buildingValue;
		}
	}

	public Building getBuildingOn(short x, short y) {
		return buildingsGrid[x + y * width];
	}
}
