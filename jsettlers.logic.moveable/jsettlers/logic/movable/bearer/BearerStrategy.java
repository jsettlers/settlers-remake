package jsettlers.logic.movable.bearer;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.buildings.spawn.Barrack;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;
import jsettlers.logic.movable.soldiers.BecomeSoldierStrategy;

public class BearerStrategy extends PathableStrategy implements IManageableBearer {
	private EBearerState state = EBearerState.JOBLESS;
	private ISPosition2D offer;
	private ISPosition2D request;
	private EMaterialType materialType;
	private EMovableType movableType;

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
		switch (state) {
		case CARRY_TAKE:
			super.getGrid().pushMaterial(super.getPos(), materialType, true);
			// no break here!
		case CARRY_DROP:
			// grid.requestMaterial(carryJob.getRequest()); FIXME implement reOffering of request
		}
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
				super.calculatePathTo(request);
				state = EBearerState.CARRY_DROP;
				break;
			case CARRY_DROP:
				super.getGrid().pushMaterial(super.getPos(), materialType, false); // target needs material => don't give it to the manager
				super.setMaterial(EMaterialType.NO_MATERIAL);
				super.getGrid().addJobless(this);
				super.setAction(EAction.NO_ACTION, -1);
				this.state = EBearerState.JOBLESS;
				break;
			case CARRY_INIT:
				super.setAction(EAction.NO_ACTION, -1); // this leads to a call of noActionEvent() handling the initialization
				break;

			case CONVERT_TAKE:
				super.getGrid().popMaterial(super.getPos(), movableType.getTool());
				super.setAction(EAction.NO_ACTION, -1);
				super.convertTo(movableType);
				break;

			default:
				super.setAction(EAction.NO_ACTION, -1);
				break;
			}
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
		JOBLESS,

		CONVERT_INIT,
		CONVERT_TAKE,

	}

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

	@Override
	public void becomeWorker(EMovableType movableType) {
		super.convertTo(movableType);
	}
	
	@Override
	public void becomeSoilder(ISPosition2D weaponPosition, Barrack barrack) {
		this.movable.setStrategy(new BecomeSoldierStrategy(getGrid(), movable, weaponPosition, barrack));
	}

	@Override
	public void becomeWorker(EMovableType movableType, ISPosition2D offer) {
		this.offer = offer;
		this.movableType = movableType;
		this.materialType = movableType.getTool();
		this.state = EBearerState.CONVERT_INIT;
	}

}
