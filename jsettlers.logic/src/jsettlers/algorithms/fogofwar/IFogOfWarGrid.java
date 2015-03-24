package jsettlers.algorithms.fogofwar;

import java.util.concurrent.ConcurrentLinkedQueue;

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

	ConcurrentLinkedQueue<? extends IViewDistancable> getMovableViewDistancables();

	ConcurrentLinkedQueue<? extends IViewDistancable> getBuildingViewDistancables();

}
