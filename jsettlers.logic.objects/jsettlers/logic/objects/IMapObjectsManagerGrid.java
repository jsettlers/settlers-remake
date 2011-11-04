package jsettlers.logic.objects;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.logic.map.newGrid.interfaces.AbstractHexMapObject;

public interface IMapObjectsManagerGrid {
	AbstractHexMapObject getMapObject(short x, short y, EMapObjectType mapObjectType);

	void setLandscape(short x, short y, ELandscapeType landscapeType);

	void addMapObject(short x, short y, AbstractHexMapObject mapObject);

	boolean isBlocked(short x, short y);

	void setBlocked(short x, short y, boolean blocked);

	AbstractHexMapObject removeMapObjectType(short x, short y, EMapObjectType mapObjectType);

	boolean removeMapObject(short x, short y, AbstractHexMapObject mapObject);

	short getWidth();

	short getHeight();

	boolean isInBounds(short x, short y);

}
