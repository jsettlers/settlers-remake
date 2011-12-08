package jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ISPosition2D;

public interface IConstructableBuilding extends IPlayerable {
	boolean tryToTakeMaterial();

	boolean isConstructionFinished();

	ISPosition2D calculateRealPoint(short dx, short dy);

	EBuildingType getBuildingType();
}
