package jsettlers.logic.movable.construction;

import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.management.workers.construction.BricklayerRequest;
import jsettlers.logic.management.workers.construction.IConstructableBuilding;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;

public class BricklayerStrategy extends PathableStrategy implements IManageableBricklayer {

	private BricklayerRequest bricklayerRequest;
	private IConstructableBuilding constructionSite;

	public BricklayerStrategy(IMovableGrid grid, Movable movable) {
		super(grid, movable);
		grid.addJobless(this);
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
			super.getGrid().addJobless(this);
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
			super.getGrid().addJobless(this);
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

	// @Override FIXME
	// public void setWorkerRequest(AbstractConstructionWorkerRequest curr) {
	// assert curr instanceof BricklayerRequest;
	// this.bricklayerRequest = (BricklayerRequest) curr;
	// this.constructionSite = null;
	// }

	@Override
	protected void stopOrStartWorking(boolean stop) {
		// TODO implement stopping of work
	}

	@Override
	protected boolean isGotoJobable() {
		return false;
	}

}
