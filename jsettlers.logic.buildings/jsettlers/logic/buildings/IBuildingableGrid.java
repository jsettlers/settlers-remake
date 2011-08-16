package jsettlers.logic.buildings;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.hex.interfaces.IHexMovable;
import jsettlers.logic.map.hex.interfaces.IHexStack;
import jsettlers.logic.objects.MapObjectsManager;

public interface IBuildingableGrid {

	byte getHeightAt(ISPosition2D currPos);

	boolean setBuilding(ISPosition2D pos, IBuilding newBuilding);

	void setPlayerAt(ISPosition2D currPos, byte player);

	short getWidth();

	short getHeight();

	void placeStack(ISPosition2D pos, IHexStack stack);

	void removeStack(ISPosition2D pos);

	IHexMovable getMovable(ISPosition2D door);

	void placeNewMovable(ISPosition2D door, IHexMovable movable);

	MapObjectsManager getMapObjectsManager();
}
