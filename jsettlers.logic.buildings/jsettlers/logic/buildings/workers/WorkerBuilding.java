package jsettlers.logic.buildings.workers;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.management.workers.building.IWorkerRequestBuilding;

public class WorkerBuilding extends Building implements IWorkerRequestBuilding {

	private ISPosition2D workAreaCenter;

	public WorkerBuilding(EBuildingType type, byte player) {
		super(type, player);
	}

	@Override
	public EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_ROOF;
	}

	@Override
	public boolean isOccupied() {
		return false;
	}

	@Override
	public int getActionImgIdx() {
		return 0;
	}

	@Override
	public void stopOrStartWorking(boolean stop) {
		// TODO Auto-generated method stub
	}

	@Override
	public ISPosition2D getDoor() {
		return super.getDoor();
	}

	@Override
	protected void positionedEvent(ISPosition2D pos) {
		this.workAreaCenter = pos;
	}

	@Override
	protected void constructionFinishedEvent() {
		grid.requestBuildingWorker(super.getBuildingType().getWorkerType(), this);
		super.placeFlag(true);
	}

	@Override
	protected void subTimerEvent() {
	}

	@Override
	public void setWorkAreaCenter(ISPosition2D workAreaCenter) {
		this.workAreaCenter = workAreaCenter;
	}

	@Override
	public ISPosition2D getWorkAreaCenter() {
		return workAreaCenter;
	}
}
