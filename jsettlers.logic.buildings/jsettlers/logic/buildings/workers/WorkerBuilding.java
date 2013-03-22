package jsettlers.logic.buildings.workers;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.WorkAreaBuilding;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IWorkerRequestBuilding;
import jsettlers.logic.player.Player;
import jsettlers.logic.stack.RequestStack;

/**
 * This class is a building with a worker that can fulfill it's job.
 * 
 * @author Andreas Eberle
 * 
 */
public class WorkerBuilding extends WorkAreaBuilding implements IWorkerRequestBuilding {
	private static final long serialVersionUID = 7050284039312172046L;

	private boolean isWorking = true;
	private IManageableWorker worker;

	public WorkerBuilding(EBuildingType type, Player player) {
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
	protected final void constructionFinishedEvent() {
		requestWorker();
	}

	private void requestWorker() {
		super.getGrid().requestBuildingWorker(super.getBuildingType().getWorkerType(), this);
	}

	@Override
	protected final void subTimerEvent() {
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
		if (super.isNotDestroyed()) {
			this.worker = worker;
			super.placeFlag(true);
			super.createWorkStacks();
		}
	}

	@Override
	public final void leaveBuilding(IManageableWorker worker) {
		if (worker == this.worker) {
			this.worker = null;
			super.placeFlag(false);
			super.releaseRequestStacks();
			requestWorker();
		} else {
			System.err.println("A worker not registered at the building wanted to leave it!");
		}
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
