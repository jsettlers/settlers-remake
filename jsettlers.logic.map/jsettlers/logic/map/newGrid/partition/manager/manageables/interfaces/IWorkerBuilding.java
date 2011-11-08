package jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ISPosition2D;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface IWorkerBuilding {
	ISPosition2D calculateRealPoint(short dx, short dy);

	EBuildingType getBuildingType();

}
