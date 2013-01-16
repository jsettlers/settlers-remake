package jsettlers.logic.map.newGrid.objects;

import java.io.Serializable;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.logic.objects.arrow.IArrowAttackableGrid;

public interface IMapObjectsManagerGrid extends Serializable, IArrowAttackableGrid {
	AbstractHexMapObject getMapObject(int x, int y, EMapObjectType mapObjectType);

	void setLandscape(short x, short y, ELandscapeType landscapeType);

	void addMapObject(short x, short y, AbstractHexMapObject mapObject);

	boolean isBlocked(short x, short y);

	void setBlocked(short x, short y, boolean blocked);

	AbstractHexMapObject removeMapObjectType(short x, short y, EMapObjectType mapObjectType);

	boolean removeMapObject(short x, short y, AbstractHexMapObject mapObject);

	short getWidth();

	short getHeight();

	boolean isInBounds(short x, short y);

	void setProtected(short x, short y, boolean protect);

	EResourceType getRessourceTypeAt(short x, short y);

	byte getRessourceAmountAt(short x, short y);

}
