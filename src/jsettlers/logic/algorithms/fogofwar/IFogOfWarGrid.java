package jsettlers.logic.algorithms.fogofwar;

import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;

/**
 * interface specifying the methods needed by the fog of war to operate on a grid.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IFogOfWarGrid {

	IMovable getMovableAt(short x, short y);

	IMapObject getMapObjectsAt(short x, short y);

}
