package jsettlers.logic.buildings.military;

import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;

public interface IBuildingOccupyableMovable {

	void setOccupyableBuilding(IOccupyableBuilding building);

	EMovableType getMovableType();

	ESoldierType getSoldierType();

	IMovable getMovable();

	void leaveOccupyableBuilding();

	void setSelected(boolean selected);
}
