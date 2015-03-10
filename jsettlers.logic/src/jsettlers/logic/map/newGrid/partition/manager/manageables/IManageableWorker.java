package jsettlers.logic.map.newGrid.partition.manager.manageables;

import jsettlers.common.movable.EMovableType;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IWorkerRequestBuilding;

public interface IManageableWorker extends IManageable {

	EMovableType getMovableType();

	void setWorkerJob(IWorkerRequestBuilding building);

	void buildingDestroyed();

}
