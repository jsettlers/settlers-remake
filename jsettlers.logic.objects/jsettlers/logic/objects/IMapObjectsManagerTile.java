package jsettlers.logic.objects;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.logic.map.hex.interfaces.AbstractHexMapObject;

/**
 * This interface represents a tile that can be used by the MapObjectsManager
 * 
 * @author Andreas Eberle
 * 
 */
public interface IMapObjectsManagerTile {

	AbstractHexMapObject getMapObject(EMapObjectType mapObjectType);

	void setLandscape(ELandscapeType landscapeType);

	void addMapObject(AbstractHexMapObject mapObject);

	boolean isBlocked();

	void setBlocked(boolean blocked);

	AbstractHexMapObject removeMapObjectType(EMapObjectType mapObjectType);

	boolean removeMapObject(AbstractHexMapObject mapObject);

}
