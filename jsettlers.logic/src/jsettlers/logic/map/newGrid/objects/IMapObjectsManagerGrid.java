package jsettlers.logic.map.newGrid.objects;

import java.io.Serializable;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.logic.objects.arrow.IArrowAttackableGrid;

public interface IMapObjectsManagerGrid extends Serializable, IArrowAttackableGrid {
	AbstractHexMapObject getMapObject(int x, int y, EMapObjectType mapObjectType);

	void setLandscape(int x, int y, ELandscapeType landscapeType);

	void addMapObject(int x, int y, AbstractHexMapObject mapObject);

	boolean isBlocked(int x, int y);

	void setBlocked(int x, int y, boolean blocked);

	AbstractHexMapObject removeMapObjectType(int x, int y, EMapObjectType mapObjectType);

	boolean removeMapObject(int x, int y, AbstractHexMapObject mapObject);

	short getWidth();

	short getHeight();

	boolean isInBounds(int x, int y);

	void setProtected(int x, int y, boolean protect);

	EResourceType getRessourceTypeAt(int x, int y);

	byte getRessourceAmountAt(int x, int y);

}
