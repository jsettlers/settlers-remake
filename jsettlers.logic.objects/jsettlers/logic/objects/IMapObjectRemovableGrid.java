package jsettlers.logic.objects;

import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.hex.interfaces.AbstractHexMapObject;

public interface IMapObjectRemovableGrid {

	void removeMapObject(ISPosition2D pos, AbstractHexMapObject mapObject);

}
