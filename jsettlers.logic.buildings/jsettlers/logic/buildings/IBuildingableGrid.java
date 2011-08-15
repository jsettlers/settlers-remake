package jsettlers.logic.buildings;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.hex.interfaces.AbstractHexMapObject;
import jsettlers.logic.map.hex.interfaces.IHexMovable;
import jsettlers.logic.map.hex.interfaces.IHexStack;

public interface IBuildingableGrid {

	byte getHeightAt(ISPosition2D currPos);

	void setBuilding(ISPosition2D pos, IBuilding newBuilding);

	void setPlayerAt(ISPosition2D currPos, byte player);

	short getWidth();

	short getHeight();

	void placeStack(ISPosition2D pos, IHexStack stack);

	void removeStack(ISPosition2D pos);

	void addMapObject(ISPosition2D flagPosition, AbstractHexMapObject flagMapObject);

	void removeMapObjectType(ISPosition2D pos, EMapObjectType mapObjectType);

	IHexMovable getMovable(ISPosition2D door);

	void placeNewMovable(ISPosition2D door, IHexMovable movable);

}
