package jsettlers.logic.buildings.military;

import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.map.newGrid.interfaces.IOccupyableBuilding;

public interface IBuildingOccupyableMovable {

	void setOccupyableBuilding(IOccupyableBuilding building);

	EMovableType getMovableType();

	ESoldierType getSoldierType();
}
