package jsettlers.logic.buildings.military;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;

public interface IOccupyableBuilding {

	void setSoldier(IBuildingOccupyableMovable soldier);

	ISPosition2D getDoor();

	void requestFailed(EMovableType movableType);

	ISPosition2D getPosition(IBuildingOccupyableMovable soldier);

	boolean isNotDestroyed();

}
