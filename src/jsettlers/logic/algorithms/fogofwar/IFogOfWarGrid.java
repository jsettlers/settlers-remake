package jsettlers.logic.algorithms.fogofwar;

import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;

public interface IFogOfWarGrid {

	short getHeight();

	short getWidth();

	IMovable getMovableAt(int x, int y);

	IMapObject getMapObjectsAt(int x, int y);

}
