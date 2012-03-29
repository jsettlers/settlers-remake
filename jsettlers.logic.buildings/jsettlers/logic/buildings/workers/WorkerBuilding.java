package jsettlers.logic.buildings.workers;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
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

	private short workAreaCenterX;
	private short workAreaCenterY;
	private boolean isWorking = true;
	private IManageableWorker worker;;

	public WorkerBuilding(EBuildingType type, byte player) {
		super(type, player);
	}

	@Override
	public final EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_ROOF;
	}

	@Override
	public final void stopOrStartWorking(boolean stop) {
		isWorking = !stop;
	}

	@Override
	protected final void positionedEvent(ShortPoint2D pos) {
		ShortPoint2D workAreaCenter = getBuildingType().getWorkcenter().calculatePoint(pos);
		workAreaCenterX = workAreaCenter.getX();
		workAreaCenterY = workAreaCenter.getY();
	}

	@Override
	protected final void constructionFinishedEvent() {
		super.getGrid().requestBuildingWorker(super.getBuildingType().getWorkerType(), this);
	}

	@Override
	protected final void subTimerEvent() {
	}

	@Override
	public final void setWorkAreaCenter(ShortPoint2D workAreaCenter) {
		this.workAreaCenterX = workAreaCenter.getX();
		this.workAreaCenterY = workAreaCenter.getY();
	}

	@Override
	public final short getWorkAreaCenterX() {
		return workAreaCenterX;
	}

	@Override
	public final short getWorkAreaCenterY() {
		return workAreaCenterY;
	}

	@Override
	protected final ShortPoint2D getWorkAreaCenter() {
		return new ShortPoint2D(workAreaCenterX, workAreaCenterY);
	}

	@Override
	public final boolean popMaterial(ShortPoint2D position, EMaterialType material) {
		for (RequestStack stack : super.getStacks()) {
			if (stack.getPosition().equals(position) && stack.getMaterialType() == material) {
				stack.pop();
				return true;
			}
		}
		return false;
	}

	@Override
	public final boolean isWorking() {
		return isWorking;
	}

	@Override
	public final void occupyBuilding(IManageableWorker worker) {
		this.worker = worker;
		super.placeFlag(true);
	}

	@Override
	protected final void killedEvent() {
		if (worker != null) {
			this.worker.buildingDestroyed();
			this.worker = null;
		}
	}

	@Override
	public final boolean isOccupied() {
		return worker != null;
	}

}
