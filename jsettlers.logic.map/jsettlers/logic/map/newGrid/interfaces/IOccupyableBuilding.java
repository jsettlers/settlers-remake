package jsettlers.logic.map.newGrid.interfaces;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.buildings.military.IBuildingOccupyableMovable;

public interface IOccupyableBuilding {

	void setSoldier(IBuildingOccupyableMovable soldier);

	ISPosition2D getDoor();

	void requestFailed(EMovableType movableType);

}
