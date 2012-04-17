package jsettlers.logic.newmovable.strategies.specialists;

import jsettlers.common.map.shapes.HexBorderArea;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.newmovable.NewMovableStrategy;

public final class GeologistStrategy extends NewMovableStrategy {
	private static final long serialVersionUID = 1L;

	private static final float ACTION1_DURATION = 1.4f;
	private static final float ACTION2_DURATION = 1.5f;

	private EPioneerState state = EPioneerState.JOBLESS;
	private ShortPoint2D centerPos;

	public GeologistStrategy(NewMovable movable) {
		super(movable);
	}

	@Override
	protected void action() {
		switch (state) {
		case JOBLESS:
			return;

		case GOING_TO_POS: {
			ShortPoint2D pos = super.getPos();

			if (centerPos == null) {
				this.centerPos = pos;
			}

			super.getStrategyGrid().setMarked(pos, false);
			if (canWorkOnPos(pos)) {
				super.getStrategyGrid().setMarked(pos, true);
				super.playAction(EAction.ACTION1, ACTION1_DURATION);
				state = EPioneerState.PLAYING_ACTION_1;
			} else {
				findWorkablePosition();
			}
		}
			break;

		case PLAYING_ACTION_1:
			super.playAction(EAction.ACTION2, ACTION2_DURATION);
			state = EPioneerState.PLAYING_ACTION_2;
			break;

		case PLAYING_ACTION_2: {
			ShortPoint2D pos = super.getPos();
			super.getStrategyGrid().setMarked(pos, false);
			if (canWorkOnPos(pos)) {
				executeAction(pos);
			}

			findWorkablePosition();
		}
			break;
		}
	}

	private void findWorkablePosition() {
		ShortPoint2D closeWorkablePos = getCloseWorkablePos();

		if (closeWorkablePos != null && super.goToPos(closeWorkablePos)) {
			super.getStrategyGrid().setMarked(closeWorkablePos, true);
			this.state = EPioneerState.GOING_TO_POS;
			return;
		}
		centerPos = null;

		ShortPoint2D pos = super.getPos();
		if (super.preSearchPath(true, pos.getX(), pos.getY(), (short) 30, ESearchType.RESOURCE_SIGNABLE)) {
			super.followPresearchedPath();
			this.state = EPioneerState.GOING_TO_POS;
			return;
		}

		this.state = EPioneerState.JOBLESS;
	}

	private final ShortPoint2D getCloseWorkablePos() {
		ShortPoint2D bestNeighbourPos = null;
		double bestNeighbourDistance = Double.MAX_VALUE; // distance from start point

		for (ShortPoint2D satelitePos : new HexBorderArea(super.getPos(), (short) 2)) {
			if (super.isValidPosition(satelitePos) && canWorkOnPos(satelitePos)) {
				double distance = Math.hypot(satelitePos.getX() - centerPos.getX(), satelitePos.getY() - centerPos.getY());
				if (distance < bestNeighbourDistance) {
					bestNeighbourDistance = distance;
					bestNeighbourPos = satelitePos;
				}
			}
		}
		return bestNeighbourPos;
	}

	private void executeAction(ShortPoint2D pos) {
		super.getStrategyGrid().executeSearchType(pos, ESearchType.RESOURCE_SIGNABLE);
	}

	private boolean canWorkOnPos(ShortPoint2D pos) {
		return super.fitsSearchType(pos, ESearchType.RESOURCE_SIGNABLE);
	}

	private static enum EPioneerState {
		JOBLESS,
		GOING_TO_POS,
		PLAYING_ACTION_1,
		PLAYING_ACTION_2
	}

	@Override
	protected void moveToPathSet(ShortPoint2D oldTargetPos, ShortPoint2D targetPos) {
		this.state = EPioneerState.GOING_TO_POS;
		centerPos = null;

		super.getStrategyGrid().setMarked(super.getPos(), false);

		if (oldTargetPos != null) {
			super.getStrategyGrid().setMarked(oldTargetPos, false);
		}
	}
}
