package jsettlers.logic.newmovable.strategies.specialists;

import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.newmovable.NewMovableStrategy;

public class PioneerStrategy extends NewMovableStrategy {
	private static final long serialVersionUID = 1L;
	private EPioneerState state = EPioneerState.JOBLESS;
	private ShortPoint2D centerPos;

	public PioneerStrategy(NewMovable movable) {
		super(movable);
	}

	@Override
	protected void action() {
		switch (state) {
		case JOBLESS:
			return;

		case GOING_TO_POS:
			if (centerPos != null) {
				this.centerPos = super.getPos();
			}

			if (canWorkOnPos(super.getPos())) {
				super.playAction(EAction.ACTION1, getAction1Duration());
			}
			state = EPioneerState.WORKING_ON_POS;
			break;

		case WORKING_ON_POS:
			if (canWorkOnPos(super.getPos())) {
				executeAction(super.getPos());
			}
			state = EPioneerState.JOBLESS;
			break;
		}
	}

	private void executeAction(ShortPoint2D pos) {
		super.getStrategyGrid().changePlayerAt(pos, super.getPlayer());
	}

	private float getAction1Duration() {
		return 1;
	}

	private boolean canWorkOnPos(ShortPoint2D pos) {
		return super.fitsSearchType(pos, ESearchType.FOREIGN_GROUND);
	}

	private static enum EPioneerState {
		JOBLESS,
		GOING_TO_POS,
		WORKING_ON_POS
	}

	@Override
	protected void moveToPathSet(ShortPoint2D targetPos) {
		this.state = EPioneerState.GOING_TO_POS;

	}

}
