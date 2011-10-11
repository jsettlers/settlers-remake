package jsettlers.logic.movable.construction;

import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.management.GameManager;
import jsettlers.logic.management.workers.IWorkerJobable;
import jsettlers.logic.management.workers.construction.AbstractConstructionWorkerRequest;
import jsettlers.logic.management.workers.construction.DiggerRequest;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;

public class DiggerStrategy extends PathableStrategy implements IWorkerJobable<AbstractConstructionWorkerRequest> {

	private DiggerRequest request;

	public DiggerStrategy(IMovableGrid grid, Movable movable) {
		super(grid, movable);
		GameManager.addJoblessWorker(this);
	}

	@Override
	public boolean needsPlayersGround() {
		return true;
	}

	@Override
	public boolean noActionEvent() {
		if (!super.noActionEvent()) {
			if (request != null) {
				tryToDigg();
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	protected void pathRequestFailed() {
		if (request != null) {
			// TODO rerequest the worker request
			request = null;
			GameManager.addJoblessWorker(this);
		}
		super.setAction(EAction.NO_ACTION, -1);
	}

	boolean wentThere = false;

	@Override
	protected boolean actionFinished() {
		if (!super.actionFinished()) {
			if (request != null) {
				executeDigg();
				tryToDigg();
			} else {
				super.setAction(EAction.NO_ACTION, -1);
			}
		}
		return true;
	}

	private void executeDigg() {
		super.getGrid().changeHeightAt(super.getPos(), (byte) (Math.signum(request.getHeightAvg() - super.getGrid().getHeightAt(super.getPos()))));
		super.getGrid().setMarked(super.getPos(), false);
	}

	private ISPosition2D getDiggablePosition() {
		for (ISPosition2D pos : request.getPositions()) {
			if (needsToChangeHeight(pos) && !super.getGrid().isMarked(pos)) {
				return pos;
			}
		}
		return null;
	}

	private boolean needsToChangeHeight(ISPosition2D pos) {
		if (super.getGrid().getHeightAt(pos) != request.getHeightAvg()) {
			return true;
		}
		return false;
	}

	@Override
	protected void pathFinished() {
		wentThere = true;
		tryToDigg();
	}

	private void tryToDigg() {
		if (needsToChangeHeight(super.getPos()) && wentThere) {
			super.setAction(EAction.ACTION1, 1);
		} else if (request != null) {
			ISPosition2D diggablePos = getDiggablePosition();
			if (diggablePos != null) {
				wentThere = false;
				super.getGrid().setMarked(diggablePos, true);
				super.calculatePathTo(diggablePos);
			} else {
				super.setAction(EAction.NO_ACTION, -1);
				request = null;
				GameManager.addJoblessWorker(this);
			}
		}
	}

	@Override
	public EMovableType getMovableType() {
		return EMovableType.DIGGER;
	}

	@Override
	public void setWorkerRequest(AbstractConstructionWorkerRequest curr) {
		assert curr instanceof DiggerRequest;
		this.request = (DiggerRequest) curr;
		wentThere = false;
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
