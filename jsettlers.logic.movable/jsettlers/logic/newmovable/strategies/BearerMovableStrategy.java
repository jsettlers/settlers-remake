package jsettlers.logic.newmovable.strategies;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IBarrack;
import jsettlers.logic.map.newGrid.partition.manager.materials.interfaces.IMaterialRequest;
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
	private IMaterialRequest request;
	private EMaterialType materialType;

	private EMovableType targetMovableType;

	private IBarrack barrack;
	private IWorkerRequester workerRequester;

	public BearerMovableStrategy(NewMovable movable) {
		super(movable);
		reportAsJobless();
	}

	private void reportAsJobless() {
		super.getStrategyGrid().addJobless(this);
	}

	@Override
	protected void action() {
		switch (state) {
		case JOBLESS: // TODO @Andreas think about new state for NewMovable to turn of downcall for action when it's not needed
			break;
		case INIT_CONVERT_WITH_TOOL_JOB:
		case INIT_CARRY_JOB:
			state = EBearerState.GOING_TO_OFFER;

			if (!super.getPos().equals(offer)) { // if we are not at the offers position, go to it.
				if (!super.goToPos(offer)) {
					handleJobFailed(true);
				}
				break;
			}
		case GOING_TO_OFFER:
			if (super.getPos().equals(offer)) {
				super.playAction(EAction.TAKE, Constants.MOVABLE_TAKE_DROP_DURATION);
				state = EBearerState.TAKING;
			} else {
				handleJobFailed(true);
			}
			break;
		case TAKING:
			if (super.getStrategyGrid().takeMaterial(super.getPos(), materialType)) {
				if (request == null) { // we handle a convert with tool job
					state = EBearerState.DEAD_OBJECT;
					super.convertTo(targetMovableType);
				} else {
					super.setMaterial(materialType);
					offer = null;
					state = EBearerState.GOING_TO_REQUEST;
					if (!super.getPos().equals(request.getPos()) && !super.goToPos(request.getPos())) {
						handleJobFailed(true);
					}
				}
			} else {
				handleJobFailed(true);
			}
			break;
		case GOING_TO_REQUEST:
			if (super.getPos().equals(request.getPos())) {
				super.playAction(EAction.DROP, Constants.MOVABLE_TAKE_DROP_DURATION);
				state = EBearerState.DROPPING;
			} else {
				handleJobFailed(true);
			}
			break;
		case DROPPING:
			super.getStrategyGrid().dropMaterial(super.getPos(), materialType, !request.isActive()); // if request is inactive, offer the material
			super.setMaterial(EMaterialType.NO_MATERIAL);
			request.deliveryFulfilled();
			request = null;
			materialType = null;
			state = EBearerState.JOBLESS;
			reportAsJobless();
			break;

		case INIT_CONVERT_JOB:
			state = EBearerState.DEAD_OBJECT;
			super.convertTo(targetMovableType);
			break;

		case INIT_BECOME_SOLDIER_JOB:
			super.goToPos(barrack.getDoor());
			state = EBearerState.GOING_TO_BARRACK;
			break;

		case GOING_TO_BARRACK:
			EMovableType movableType = barrack.popWeaponForBearer();
			if (movableType == null) { // weapon got missing, make this bearer jobless again
				this.barrack = null;
				this.state = EBearerState.JOBLESS;
				reportAsJobless();
			} else {
				this.state = EBearerState.DEAD_OBJECT;
				super.convertTo(movableType);
				super.goToPos(barrack.getSoldierTargetPosition());
			}
			break;

		case DEAD_OBJECT:
			assert false : "we should never get here!";
		}
	}

	private void handleJobFailed(boolean reportAsJobless) {
		switch (state) {
		case INIT_CARRY_JOB:
		case GOING_TO_OFFER:
			reoffer();
		case TAKING:
			if (targetMovableType != null) {
				workerRequester.workerCreationRequestFailed(targetMovableType, super.getPos());
			}
		case GOING_TO_REQUEST:
			if (request != null) {
				request.deliveryAborted();
			}
			break;

		case INIT_BECOME_SOLDIER_JOB:
		case GOING_TO_BARRACK:
			barrack.bearerRequestFailed();
			break;

		case DROPPING: // handled after this
			break;

		case INIT_CONVERT_WITH_TOOL_JOB:
			reoffer();
		case INIT_CONVERT_JOB:
			workerRequester.workerCreationRequestFailed(targetMovableType, super.getPos());
			break;

		case DEAD_OBJECT:
			break;
		case JOBLESS:
			break;
		default:
			break;
		}

		EMaterialType carriedMaterial = super.setMaterial(EMaterialType.NO_MATERIAL);
		if (carriedMaterial != EMaterialType.NO_MATERIAL) {
			super.getStrategyGrid().dropMaterial(super.getPos(), materialType, true);
		}

		offer = null;
		request = null;
		materialType = null;
		targetMovableType = null;
		workerRequester = null;
		state = EBearerState.JOBLESS;

		if (reportAsJobless) {
			reportAsJobless();
		}
	}

	private void reoffer() {
		super.getStrategyGrid().takeMaterial(offer, materialType);
		super.getStrategyGrid().dropMaterial(offer, materialType, true);
	}

	@Override
	protected boolean checkPathStepPreconditions(ShortPoint2D pathTarget, int step) {
		return request == null || request.isActive();
	}

	@Override
	public boolean deliver(EMaterialType materialType, ShortPoint2D offer, IMaterialRequest request) {
		if (state == EBearerState.JOBLESS) {
			this.offer = offer;
			this.request = request;
			this.materialType = materialType;

			this.state = EBearerState.INIT_CARRY_JOB;
			request.deliveryAccepted();

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void becomeWorker(IWorkerRequester requester, EMovableType movableType) {
		this.workerRequester = requester;
		this.targetMovableType = movableType;
		this.state = EBearerState.INIT_CONVERT_JOB;
		this.offer = null;
		this.materialType = null;
	}

	@Override
	public void becomeWorker(IWorkerRequester requester, EMovableType movableType, ShortPoint2D offer) {
		this.workerRequester = requester;
		this.targetMovableType = movableType;
		this.offer = offer;
		this.materialType = movableType.getTool();
		this.state = EBearerState.INIT_CONVERT_WITH_TOOL_JOB;
	}

	@Override
	public void becomeSoldier(IBarrack barrack) {
		this.barrack = barrack;
		this.state = EBearerState.INIT_BECOME_SOLDIER_JOB;
	}

	@Override
	protected void strategyKilledEvent(ShortPoint2D pathTarget) {
		if (state == EBearerState.JOBLESS) {
			super.getStrategyGrid().removeJobless(this);
		} else {
			handleJobFailed(false);
		}
		state = EBearerState.DEAD_OBJECT;
	}

	public boolean isDead() {
		return state == EBearerState.DEAD_OBJECT;
	}

	/**
	 * This enum defines the internal states of a bearer.
	 * 
	 * @author Andreas Eberle
	 * 
	 */
	private enum EBearerState {
		JOBLESS,

		INIT_CARRY_JOB,
		GOING_TO_REQUEST,
		GOING_TO_OFFER,
		TAKING,
		DROPPING,

		INIT_CONVERT_JOB,
		INIT_CONVERT_WITH_TOOL_JOB,

		DEAD_OBJECT,

		INIT_BECOME_SOLDIER_JOB,
		GOING_TO_BARRACK,
	}

}
