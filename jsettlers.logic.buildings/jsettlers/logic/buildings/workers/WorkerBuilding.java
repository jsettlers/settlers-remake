package jsettlers.logic.buildings.workers;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IWorkerRequestBuilding;
import jsettlers.logic.stack.RequestStack;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class WorkerBuilding extends Building implements IWorkerRequestBuilding {
	private static final long serialVersionUID = 7050284039312172046L;

	private ISPosition2D workAreaCenter;

	private boolean isWorking = true;

	private IManageableWorker worker;;

	public WorkerBuilding(EBuildingType type, byte player) {
		super(type, player);
	}

	@Override
	public EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_ROOF;
	}

	@Override
	public void stopOrStartWorking(boolean stop) {
		isWorking = !stop;
	}

	@Override
	public ISPosition2D getDoor() {
		return super.getDoor();
	}

	@Override
	protected void positionedEvent(ISPosition2D pos) {
		workAreaCenter = getBuildingType().getWorkcenter().calculatePoint(pos);
	}

	@Override
	protected void constructionFinishedEvent() {
		grid.requestBuildingWorker(super.getBuildingType().getWorkerType(), this);
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

	@Override
	public boolean popMaterial(ISPosition2D position, EMaterialType material) {
		for (RequestStack stack : super.stacks) {
			if (stack.getPosition().equals(position) && stack.getMaterialType() == material) {
				stack.pop();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isWorking() {
		return isWorking;
	}

	@Override
	public void occupyBuilding(IManageableWorker worker) {
		this.worker = worker;
		super.placeFlag(true);
	}

	@Override
	protected void killedEvent() {
		if (worker != null) {
			this.worker.buildingDestroyed();
			this.worker = null;
		}
	}

}
