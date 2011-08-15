package jsettlers.logic.movable.construction;

import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.management.GameManager;
import jsettlers.logic.management.workers.IWorkerJobable;
import jsettlers.logic.management.workers.construction.AbstractConstructionWorkerRequest;
import jsettlers.logic.management.workers.construction.BricklayerRequest;
import jsettlers.logic.management.workers.construction.IConstructableBuilding;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;

public class BricklayerStrategy extends PathableStrategy implements IWorkerJobable<AbstractConstructionWorkerRequest> {

	private BricklayerRequest bricklayerRequest;
	private IConstructableBuilding constructionSite;

	public BricklayerStrategy(Movable movable) {
		super(movable);
		GameManager.addJoblessWorker(this);
	}

	@Override
	public boolean needsPlayersGround() {
		return true;
	}

	@Override
	protected boolean noActionEvent() {
		if (!super.noActionEvent()) {
			if (bricklayerRequest != null) {
				super.calculatePathTo(bricklayerRequest.getPos());
			}
		}
		return true;
	}

	@Override
	protected void pathRequestFailed() {
		if (bricklayerRequest != null) {
			// TODO rerequest the worker request
			bricklayerRequest = null;
			GameManager.addJoblessWorker(this);
		}
	}

	@Override
	protected boolean actionFinished() {
		if (!super.actionFinished()) {
			if (constructionSite != null) {
				tryToBuild();
			} else {
				super.setAction(EAction.NO_ACTION, -1);
			}
		}
		return true;
	}

	private void tryToBuild() {
		if (constructionSite.tryToTakeMaterial()) {
			super.setAction(EAction.ACTION1, 1);
		} else {
			constructionSite = null;
			GameManager.addJoblessWorker(this);
			super.setAction(EAction.NO_ACTION, -1);
		}
	}

	@Override
	protected void pathFinished() {
		if (bricklayerRequest != null) {
			constructionSite = bricklayerRequest.getConstructionSite();
			super.setDirection(bricklayerRequest.getLookDirection());
			bricklayerRequest = null;
			tryToBuild();
		} else {
			super.setAction(EAction.NO_ACTION, -1);
		}
	}

	@Override
	public EMovableType getMovableType() {
		return EMovableType.BRICKLAYER;
	}

	@Override
	public void setWorkerRequest(AbstractConstructionWorkerRequest curr) {
		assert curr instanceof BricklayerRequest;
		this.bricklayerRequest = (BricklayerRequest) curr;
		this.constructionSite = null;
	}

	@Override
	protected void stopOrStartWorking(boolean stop) {
		// TODO implement stopping of work
	}

	@Override
	protected boolean isGotoJobable() {
		return false;
	}

}
