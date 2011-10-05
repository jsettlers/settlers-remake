package jsettlers.logic.map.newGrid.objects;

import jsettlers.common.mapobject.IMapObject;

/**
 * This grid stores the objects located at each position.
 * 
 * @author Andreas Eberle
 * 
 */
public class ObjectsGrid {
	private final IMapObject[][] objectsGrid;

	public ObjectsGrid(short width, short height) {
		this.objectsGrid = new IMapObject[width][height];
	}

	public IMapObject getObjectsAt(short x, short y) {
		return objectsGrid[x][y];
	}

}
