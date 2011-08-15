package jsettlers.logic.movable.go;

import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;

public class GotoTestStrategy extends PathableStrategy {

	protected GotoTestStrategy(Movable movable) {
		super(movable);
	}

	@Override
	public boolean needsPlayersGround() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean isGotoJobable() {
		return true;
	}
}
