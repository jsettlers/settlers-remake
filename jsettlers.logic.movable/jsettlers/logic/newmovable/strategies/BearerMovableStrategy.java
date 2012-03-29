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
	private ShortPoint2D offer;
	private IMaterialRequester requester;
	private EMaterialType materialType;
	private EBearerState state;

	protected BearerMovableStrategy(NewMovable movable) {
		super(movable);
		super.getStrategyGrid().addJoblessBearer(this);
	}

	@Override
	protected void action() {
		switch (state) {
		case JOBLESS: // TODO @Andreas think about new state for NewMovable to turn of downcall for action when it's not needed
			break;
		case INIT_NEW_JOB:
			state = EBearerState.GOING_TO_OFFER;
			if (!super.goToPos(offer)) {
				handleJobFailed();
			}
			break;
		case GOING_TO_OFFER:
			super.playAction(EAction.TAKE, Constants.MOVABLE_TAKE_DROP_DURATION);
			state = EBearerState.TAKING;
			break;
		case TAKING:
			if (super.getStrategyGrid().takeMaterial(super.getPos(), materialType)) {
				super.setMaterial(materialType);
				state = EBearerState.GOING_TO_REQUEST;
				// if (!super.goToPos(requester.getPos())) { FIXME
				// handleJobFailed();
				// }
			} else {
				handleJobFailed();
			}
		}
	}

	private void handleJobFailed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeJob(ShortPoint2D offer, IMaterialRequester requester, EMaterialType materialType) {
		this.offer = offer;
		this.requester = requester;
		this.materialType = materialType;

		this.state = EBearerState.INIT_NEW_JOB;
	}

	@Override
	public void becomeWorker(EMovableType movableType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void becomeWorker(EMovableType movableType, ShortPoint2D offer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void becomeSoldier(Barrack barrack) {
		// TODO Auto-generated method stub

	}

	private enum EBearerState {
		JOBLESS,
		GOING_TO_REQUEST,
		INIT_NEW_JOB,
		GOING_TO_OFFER,
		TAKING,
	}
}
