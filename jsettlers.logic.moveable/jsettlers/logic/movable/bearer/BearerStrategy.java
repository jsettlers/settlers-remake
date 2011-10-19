package jsettlers.logic.movable.bearer;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.management.bearer.job.BearerToWorkerWithMaterialJob;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;

public class BearerStrategy extends PathableStrategy implements IManageableBearer {
	private EBearerState state = EBearerState.JOBLESS;
	private BearerToWorkerWithMaterialJob toWorkerJob;
	private ISPosition2D offer;
	private ISPosition2D request;
	private EMaterialType materialType;

	public BearerStrategy(IMovableGrid grid, Movable movable) {
		super(grid, movable);
		grid.addJobless(this);
	}

	@Override
	public boolean needsPlayersGround() {
		return true;
	}

	private void initJob() {
		switch (state) {
		case CARRY_INIT:
			state = EBearerState.CARRY_TAKE;
			super.calculatePathTo(offer);
			break;

		default:
			assert false : "wrong state for initializing job";
		}

		// (state == EBearerState.CARRY_INIT) {
		//
		// } else if (this.toWorkerJob != null) {
		// state = EBearerState.TAKE;
		// super.calculatePathTo(toWorkerJob.getFirstPos());
		// }
	}

	@Override
	protected void pathRequestFailed() {
		switch (state) {
		case CARRY_TAKE:
			super.getGrid().pushMaterial(super.getPos(), materialType);
			// no break here!
		case CARRY_DROP:
			// grid.requestMaterial(carryJob.getRequest()); FIXME implement reOffering of request
		}
		// } else if (toWorkerJob != null) {
		// super.getGrid().pushMaterial(super.getPos(), toWorkerJob.getOffer().getMaterialType());
		// // GameManager.requestMovable(toWorkerJob.getMovableType(), super.getPlayer()); FIXME
		// }
	}

	@Override
	protected void pathFinished() {
		switch (state) {
		case CARRY_TAKE:
			super.setAction(EAction.TAKE, Constants.MOVABLE_TAKE_DROP_DURATION);
			super.setMaterial(materialType);
			break;
		case CARRY_DROP:
			super.setAction(EAction.DROP, Constants.MOVABLE_TAKE_DROP_DURATION);
			break;
		}
		// } else if (toWorkerJob != null) {
		// super.setAction(EAction.TAKE, Constants.MOVABLE_TAKE_DROP_DURATION);
		// } else {
		// System.err.println("BearerStrategy.pathFinished() called, but no job set");
		// }
	}

	@Override
	protected boolean noActionEvent() {
		if (!super.noActionEvent()) {
			if (state == EBearerState.CARRY_INIT) {
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
			switch (state) {
			case CARRY_TAKE:
				super.getGrid().popMaterial(super.getPos(), materialType);
				super.calculatePathTo(request);
				state = EBearerState.CARRY_DROP;
				break;

			case CARRY_DROP:
				super.getGrid().pushMaterial(super.getPos(), materialType);
				this.state = EBearerState.JOBLESS;
				super.setAction(EAction.NO_ACTION, -1);
				super.setMaterial(EMaterialType.NO_MATERIAL);
				super.getGrid().addJobless(this);
				break;
			case CARRY_INIT:
				super.setAction(EAction.NO_ACTION, -1); // this leads to a call of noActionEvent() handling the initialization
			default:
				super.setAction(EAction.NO_ACTION, -1);
			}
			// } else if (toWorkerJob != null) {
			// super.getGrid().popMaterial(super.getPos(), toWorkerJob.getOffer().getMaterialType());
			// toWorkerJob.getOffer().setFulfilled();
			// super.setAction(EAction.NO_ACTION, -1);
			// super.convertTo(toWorkerJob.getMovableType());
			// toWorkerJob = null;
			// } else {
			// super.setAction(EAction.NO_ACTION, -1);
			// }
		}
		return true;
	}

	@Override
	public EMovableType getMovableType() {
		return EMovableType.BEARER;
	}

	public static enum EBearerState {
		CARRY_INIT,
		CARRY_TAKE,
		CARRY_DROP,
		JOBLESS

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
		this.offer = offer;
		this.request = request;
		this.materialType = materialType;
		this.state = EBearerState.CARRY_INIT;
	}

}
