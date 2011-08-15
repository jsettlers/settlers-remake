package jsettlers.logic.management.workers.building;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.management.workers.AbstractWorkerRequest;

public class BuildingWorkerRequest extends AbstractWorkerRequest {

	private final IWorkerRequestBuilding building;

	public BuildingWorkerRequest(EMovableType movableType, IWorkerRequestBuilding building) {
		super(movableType, building.getPlayer());
		this.building = building;
	}

	@Override
	public EMovableType getWorkerType() {
		return movableType;
	}

	@Override
	public ISPosition2D getPos() {
		return building.getDoor();
	}

	public IWorkerRequestBuilding getBuilding() {
		return building;
	}
}
