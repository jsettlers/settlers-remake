package jsettlers.logic.map.newGrid.partition.manager.manageables;

import jsettlers.common.movable.EMovableType;
import jsettlers.logic.management.workers.building.IWorkerRequestBuilding;

public interface IManageableWorker extends IManageable {

	EMovableType getMovableType();

	void setWorkerJob(IWorkerRequestBuilding building);

}
