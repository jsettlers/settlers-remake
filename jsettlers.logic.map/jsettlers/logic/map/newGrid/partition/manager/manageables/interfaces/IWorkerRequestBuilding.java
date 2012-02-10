package jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;

/**
 * interface for a building that want's to request a worker
 * 
 * @author Andreas Eberle
 * 
 */
public interface IWorkerRequestBuilding extends IPlayerable {

	/**
	 * 
	 * @return gives the door position of the building. (Needed to determine the nearest settler)
	 */
	ISPosition2D getDoor();

	short getWorkAreaCenterX();

	short getWorkAreaCenterY();

	boolean popMaterial(ISPosition2D position, EMaterialType material);

	void occupyBuilding(IManageableWorker worker);

	ISPosition2D calculateRealPoint(short dx, short dy);

	EBuildingType getBuildingType();

	boolean isNotDestroyed();
}
