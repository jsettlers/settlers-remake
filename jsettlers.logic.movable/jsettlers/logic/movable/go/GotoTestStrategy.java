package jsettlers.logic.movable.go;

import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;

public class GotoTestStrategy extends PathableStrategy {

	protected GotoTestStrategy(IMovableGrid grid, Movable movable) {
		super(grid, movable);
	}

	@Override
	public boolean needsPlayersGround() {
		return false;
	}

	@Override
	public void pathRequestFailed() {
		// nothing to do
	}

	@Override
	protected EMovableType getMovableType() {
		return EMovableType.BEARER;
	}

	@Override
	protected void pathFinished() {
		super.setAction(EAction.NO_ACTION, -1);
	}

	@Override
	protected void stopOrStartWorking(boolean stop) {
	}

	@Override
	protected boolean isGotoJobable() {
		return true;
	}
}
