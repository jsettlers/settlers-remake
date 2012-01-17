package jsettlers.logic.movable.bearer;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.buildings.military.Barrack;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IMaterialRequester;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;

public class BearerStrategy extends PathableStrategy implements IManageableBearer {
	private static final long serialVersionUID = -3470280673016494554L;

	private EBearerState state = EBearerState.JOBLESS;
	private ISPosition2D offer;
	private IMaterialRequester requester;
	private EMaterialType materialType;
	private EMovableType movableType;
	private Barrack barrack;

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
		pathAbortedEvent();
	}

	@Override
	protected void pathFinished() {
		switch (state) {
		case CARRY_TAKE:
		case CONVERT_TAKE:
			super.setAction(EAction.TAKE, Constants.MOVABLE_TAKE_DROP_DURATION);
			super.setMaterial(materialType);
			break;

		case CARRY_DROP:
			super.setAction(EAction.DROP, Constants.MOVABLE_TAKE_DROP_DURATION);
			break;

		case SOLDIER_CONVERT:
			EMovableType movableType = barrack.popWeaponForBearer();
			if (movableType == null) { // weapon got missing, make us a bearer again
				this.state = EBearerState.JOBLESS;
				this.barrack = null;
				super.getGrid().addJobless(this);
			} else {
				super.convertTo(movableType);
			}
			super.setAction(EAction.NO_ACTION, -1);
			break;

		default:
			super.setAction(EAction.NO_ACTION, -1); // can happen if bearer leaves protected position
		}
	}

	@Override
	protected boolean noActionEvent() {
		if (!super.noActionEvent()) {
			switch (state) {
			case CARRY_INIT:
				this.state = EBearerState.CARRY_TAKE;
				super.calculatePathTo(offer);
				return true;

			case CONVERT_INIT:
				this.state = EBearerState.CONVERT_TAKE;
				super.calculatePathTo(offer);
				return true;

			case SOLDIER_INIT:
				this.state = EBearerState.SOLDIER_CONVERT;
				super.calculatePathTo(barrack.getDoor());
				return true;

			default:
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
				super.calculatePathTo(requester.getPos());
				state = EBearerState.CARRY_DROP;
				break;
			case CARRY_DROP:
				super.getGrid().pushMaterial(super.getPos(), materialType, false); // target needs material => don't give it to the manager
				super.setMaterial(EMaterialType.NO_MATERIAL);
				resetJob();
				break;
			case CARRY_INIT:
				super.setAction(EAction.NO_ACTION, -1); // this leads to a call of noActionEvent() handling the initialization
				break;

			case CONVERT_TAKE:
				super.getGrid().popMaterial(super.getPos(), movableType.getTool());
				super.setAction(EAction.NO_ACTION, -1);
				super.convertTo(movableType);
				break;

			case ABORTED:
				if (materialType != null) {
					super.getGrid().pushMaterial(super.getPos(), materialType, true);
					super.setMaterial(EMaterialType.NO_MATERIAL);
				}
				resetJob();
				break;

			default:
				super.setAction(EAction.NO_ACTION, -1);
				break;
			}
		}
		return true;
	}

	private void resetJob() {
		this.state = EBearerState.JOBLESS;
		super.getGrid().addJobless(this);
		super.setAction(EAction.NO_ACTION, -1);
		this.requester = null;
	}

	@Override
	public EMovableType getMovableType() {
		return EMovableType.BEARER;
	}

	public static enum EBearerState {
		CARRY_INIT,
		CARRY_TAKE,
		CARRY_DROP,
		JOBLESS,
		ABORTED,

		CONVERT_INIT,
		CONVERT_TAKE,

		SOLDIER_INIT,
		SOLDIER_CONVERT,

	}

	@Override
	protected boolean isPathStopable() {
		return false;
	}

	@Override
	protected boolean isGotoJobable() {
		return false;
	}

	@Override
	protected boolean checkGoStepPrecondition() {
		return requester == null || requester.isRequestActive();
	}

	@Override
	protected void pathAbortedEvent() {
		switch (state) {
		case CARRY_DROP:
			this.state = EBearerState.ABORTED;
			super.setAction(EAction.DROP, Constants.MOVABLE_TAKE_DROP_DURATION);
			this.requester.requestFailed();
			break;

		default:
			resetJob();
			break;
		}

		this.requester = null;
	}

	@Override
	protected void killedEvent() {
		if (requester != null) {
			requester.requestFailed();
		}
	}

	@Override
	public void executeJob(ISPosition2D offer, IMaterialRequester requester, EMaterialType materialType) {
		this.offer = offer;
		this.requester = requester;
		this.materialType = materialType;
		this.state = EBearerState.CARRY_INIT;
	}

	@Override
	public void becomeWorker(EMovableType movableType) {
		super.convertTo(movableType);
	}

	@Override
	public void becomeSoldier(Barrack barrack) {
		this.barrack = barrack;
		this.state = EBearerState.SOLDIER_INIT;
	}

	@Override
	public void becomeWorker(EMovableType movableType, ISPosition2D offer) {
		this.offer = offer;
		this.movableType = movableType;
		this.materialType = movableType.getTool();
		this.state = EBearerState.CONVERT_INIT;
	}

}
