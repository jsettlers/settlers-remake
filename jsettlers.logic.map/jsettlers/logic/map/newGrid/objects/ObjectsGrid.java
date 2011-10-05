package jsettlers.logic.map.newGrid.objects;

import jsettlers.common.mapobject.IMapObject;

/**
 * This grid stores the objects located at each position.
 * 
 * @author Andreas Eberle
 * 
 */
public class ObjectsGrid {
	private final IMapObject[][] grid;

	public ObjectsGrid(short width, short height) {
		this.grid = new IMapObject[width][height];
	}

}
