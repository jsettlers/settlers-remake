package jsettlers.logic.map.newGrid.partition.manager.objects;

import java.io.Serializable;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IWorkerRequestBuilding;

public final class WorkerRequest implements ILocatable, Serializable {
	private static final long serialVersionUID = 6420250669583553112L;

	public final EMovableType movableType;
	public final IWorkerRequestBuilding building;
	public boolean creationRequested = false;

	public WorkerRequest(EMovableType movableType, IWorkerRequestBuilding building) {
		this.building = building;
		this.movableType = movableType;
	}

	@Override
	public ShortPoint2D getPos() {
		return building.getDoor();
	}

	@Override
	public String toString() {
		return movableType + "    " + creationRequested + "     " + building.getBuildingType();
	}
}