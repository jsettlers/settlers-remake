package jsettlers.logic.newmovable.strategies;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.military.Barrack;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IMaterialRequester;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.newmovable.NewMovableStrategy;

/**
 * Strategy for bearers.
 * 
 * @author Andreas Eberle
 * 
 */
public final class BearerMovableStrategy extends NewMovableStrategy implements IManageableBearer {
	private static final long serialVersionUID = -734268451796522451L;

	private EBearerState state = EBearerState.JOBLESS;

	private ShortPoint2D offer;
	private IMaterialRequester requester;
	private EMaterialType materialType;

	private EMovableType targetMovableType;

	public BearerMovableStrategy(NewMovable movable) {
		super(movable);
		super.getStrategyGrid().addJoblessBearer(this);
	}

	@Override
	protected void action() {
		switch (state) {
		case JOBLESS: // TODO @Andreas think about new state for NewMovable to turn of downcall for action when it's not needed
			break;
		case INIT_CONVERT_WITH_TOOL_JOB:
		case INIT_CARRY_JOB:
			state = EBearerState.GOING_TO_OFFER;
			if (!super.goToPos(offer)) {
				handleJobFailed();
			}
			break;
		case GOING_TO_OFFER:
			if (super.getPos().equals(offer)) {
				super.playAction(EAction.TAKE, Constants.MOVABLE_TAKE_DROP_DURATION);
				state = EBearerState.TAKING;
			} else {
				handleJobFailed();
			}
			break;
		case TAKING:
			if (super.getStrategyGrid().takeMaterial(super.getPos(), materialType)) {
				if (requester == null) { // we handle a convert with tool job
					super.convertTo(targetMovableType);
					state = EBearerState.DEAD_OBJECT;
				} else {
					super.setMaterial(materialType);
					offer = null;
					state = EBearerState.GOING_TO_REQUEST;
					if (!super.goToPos(requester.getPos())) {
						handleJobFailed();
					}
				}
			} else {
				handleJobFailed();
			}
			break;
		case GOING_TO_REQUEST:
			if (super.getPos().equals(requester.getPos())) {
				super.playAction(EAction.DROP, Constants.MOVABLE_TAKE_DROP_DURATION);
				state = EBearerState.DROPPING;
			} else {
				handleJobFailed();
			}
			break;
		case DROPPING:
			drop(materialType);
			super.setMaterial(EMaterialType.NO_MATERIAL);
			requester = null;
			materialType = null;
			state = EBearerState.JOBLESS;
			super.getStrategyGrid().addJoblessBearer(this);
			break;

		case INIT_CONVERT_JOB:
			super.convertTo(targetMovableType);
			state = EBearerState.DEAD_OBJECT;
			break;

		case DEAD_OBJECT:
			assert false : "we should never get here!";
		}
	}

	private void handleJobFailed() {
		switch (state) {
		case INIT_CARRY_JOB:
		case GOING_TO_OFFER:
			// TODO @Andreas reoffer the offer
		case TAKING:
		case GOING_TO_REQUEST:
			if (requester != null && requester.isRequestActive()) {
				requester.requestFailed();
			}
			break;
		}

		EMaterialType carriedMaterial = super.setMaterial(EMaterialType.NO_MATERIAL);
		if (carriedMaterial != EMaterialType.NO_MATERIAL) {
			drop(carriedMaterial);
		}

		offer = null;
		requester = null;
		materialType = null;
		targetMovableType = null;
		state = EBearerState.JOBLESS;
		super.getStrategyGrid().addJoblessBearer(this);
	}

	private void drop(EMaterialType materialType) {
		super.getStrategyGrid().dropMaterial(super.getPos(), materialType);
	}

	@Override
	protected boolean checkPathStepPreconditions() {
		return requester == null || requester.isRequestActive();
	}

	@Override
	public void executeJob(ShortPoint2D offer, IMaterialRequester requester, EMaterialType materialType) {
		this.offer = offer;
		this.requester = requester;
		this.materialType = materialType;

		this.state = EBearerState.INIT_CARRY_JOB;
	}

	@Override
	public void becomeWorker(EMovableType movableType) {
		this.targetMovableType = movableType;
		this.state = EBearerState.INIT_CONVERT_JOB;
		this.offer = null;
		this.requester = null;
		this.materialType = null;
	}

	@Override
	public void becomeWorker(EMovableType movableType, ShortPoint2D offer) {
		this.targetMovableType = movableType;
		this.offer = offer;
		this.requester = null;
		this.materialType = movableType.getTool();
		this.state = EBearerState.INIT_CONVERT_WITH_TOOL_JOB;
	}

	@Override
	public void becomeSoldier(Barrack barrack) {
		// TODO Auto-generated method stub

	}

	private enum EBearerState {
		JOBLESS,
		GOING_TO_REQUEST,
		INIT_CARRY_JOB,
		GOING_TO_OFFER,
		TAKING,
		DROPPING,
		INIT_CONVERT_JOB,
		INIT_CONVERT_WITH_TOOL_JOB,
		DEAD_OBJECT,
	}
}
