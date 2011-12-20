package jsettlers.logic.buildings.military;

import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;

public interface IBuildingOccupyableMovable {

	void setOccupyableBuilding(IOccupyableBuilding building);

	EMovableType getMovableType();

	ESoldierType getSoldierType();

	IMovable getMovable();

	void leaveOccupyableBuilding(ISPosition2D pos);

	void setSelected(boolean selected);
}
