package jsettlers.logic.movable.bearer;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.management.bearer.job.BearerCarryJob;
import jsettlers.logic.management.bearer.job.BearerToWorkerWithMaterialJob;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;

public class BearerStrategy extends PathableStrategy implements IManageableBearer {
	private EBearerState state;
	private BearerCarryJob carryJob;
	private BearerToWorkerWithMaterialJob toWorkerJob;

	public BearerStrategy(IMovableGrid grid, Movable movable) {
		super(grid, movable);
		grid.addJobless(this);
	}

	@Override
	public boolean needsPlayersGround() {
		return true;
	}

	@Override
	protected void pathRequestFailed() {
		if (carryJob != null) {
			switch (state) {
			case TAKE:
				super.getGrid().pushMaterial(super.getPos(), carryJob.getOffer().getMaterialType());
				// no break here!
			case DROP:
				// grid.requestMaterial(carryJob.getRequest()); FIXME implement reOffering of request
			}
		} else if (toWorkerJob != null) {
			super.getGrid().pushMaterial(super.getPos(), toWorkerJob.getOffer().getMaterialType());
			// GameManager.requestMovable(toWorkerJob.getMovableType(), super.getPlayer()); FIXME
		}
	}

	@Override
	protected void pathFinished() {
		if (carryJob != null) {
			switch (state) {
			case TAKE:
				super.setAction(EAction.TAKE, Constants.MOVABLE_TAKE_DROP_DURATION);
				super.setMaterial(carryJob.getOffer().getMaterialType());
				break;
			case DROP:
				super.setAction(EAction.DROP, Constants.MOVABLE_TAKE_DROP_DURATION);
				break;
			}
		} else if (toWorkerJob != null) {
			super.setAction(EAction.TAKE, Constants.MOVABLE_TAKE_DROP_DURATION);
		} else {
			System.err.println("BearerStrategy.pathFinished() called, but no job set");
		}
	}

	@Override
	protected boolean noActionEvent() {
		if (!super.noActionEvent()) {
			if (state == EBearerState.INIT) {
				initJob();
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	@Override
	protected boolean actionFinished() {
		if (!super.actionFinished()) {// the action was not connected to PathableStrategy, so its for us
			if (carryJob != null) {
				switch (state) {
				case TAKE:
					super.getGrid().popMaterial(super.getPos(), carryJob.getOffer().getMaterialType());
					carryJob.getOffer().setFulfilled();
					super.calculatePathTo(carryJob.getRequest().getPos());
					state = EBearerState.DROP;
					break;

				case DROP:
					super.getGrid().pushMaterial(super.getPos(), carryJob.getRequest().getMaterialType());
					carryJob.getRequest().setFulfilled();
					super.setAction(EAction.NO_ACTION, -1);
					super.setMaterial(EMaterialType.NO_MATERIAL);
					carryJob = null;
					super.getGrid().addJobless(this);
					break;
				case INIT:
					super.setAction(EAction.NO_ACTION, -1); // this leads to a call of noActionEvent() handling the initialization
				}
			} else if (toWorkerJob != null) {
				super.getGrid().popMaterial(super.getPos(), toWorkerJob.getOffer().getMaterialType());
				toWorkerJob.getOffer().setFulfilled();
				super.setAction(EAction.NO_ACTION, -1);
				super.convertTo(toWorkerJob.getMovableType());
				toWorkerJob = null;
			} else {
				super.setAction(EAction.NO_ACTION, -1);
			}
		}
		return true;
	}

	@Override
	public EMovableType getMovableType() {
		return EMovableType.BEARER;
	}

	public static enum EBearerState {
		TAKE,
		DROP,
		INIT

	}

	private void initJob() {
		if (this.carryJob != null) {
			state = EBearerState.TAKE;
			super.calculatePathTo(carryJob.getOffer().getPos());
		} else if (this.toWorkerJob != null) {
			state = EBearerState.TAKE;
			super.calculatePathTo(toWorkerJob.getFirstPos());
		}
	}

	// @Override
	// public void setToWorkerJob(BearerToWorkerJob job) {
	// BearerToWorkerJob toWorkerJob = job;
	// if (!(job instanceof BearerToWorkerWithMaterialJob)) {
	// super.convertTo(toWorkerJob.getMovableType());
	// } else {
	// this.toWorkerJob = (BearerToWorkerWithMaterialJob) toWorkerJob;
	// this.state = EBearerState.INIT;
	// }
	// }
	//
	// @Override
	// public void setCarryJob(BearerCarryJob job) {
	// this.carryJob = job;
	// this.state = EBearerState.INIT;
	// }

	@Override
	protected void stopOrStartWorking(boolean stop) {
		// don't care
	}

	@Override
	protected boolean isGotoJobable() {
		return false;
	}

	@Override
	public void executeJob(ISPosition2D offer, ISPosition2D request, EMaterialType materialType) {
		// TODO Auto-generated method stub

	}

}
