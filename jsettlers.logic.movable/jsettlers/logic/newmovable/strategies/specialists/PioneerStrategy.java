package jsettlers.logic.newmovable.strategies.specialists;

import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.newmovable.NewMovableStrategy;

public final class PioneerStrategy extends NewMovableStrategy {
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
			if (centerPos == null) {
				this.centerPos = super.getPos();
			}

			if (canWorkOnPos(super.getPos())) {
				super.playAction(EAction.ACTION1, getAction1Duration());
				state = EPioneerState.WORKING_ON_POS;
			} else {
				findWorkablePosition();
			}
			break;

		case WORKING_ON_POS:
			if (canWorkOnPos(super.getPos())) {
				executeAction(super.getPos());
			}

			findWorkablePosition();
			break;
		}
	}

	private void findWorkablePosition() {
		EDirection closeForeignTileDir = getCloseForeignTile();

		if (closeForeignTileDir != null && super.goInDirection(closeForeignTileDir)) {
			this.state = EPioneerState.GOING_TO_POS;
			return;
		}
		centerPos = null;

		ShortPoint2D pos = super.getPos();
		if (super.preSearchPath(true, pos.getX(), pos.getY(), (short) 30, ESearchType.FOREIGN_GROUND)) {
			super.followPresearchedPath();
			this.state = EPioneerState.GOING_TO_POS;
			return;
		}

		this.state = EPioneerState.JOBLESS;
	}

	private final EDirection getCloseForeignTile() {
		EDirection bestNeighbourDir = null;
		double bestNeighbourDistance = Double.MAX_VALUE; // distance from start point

		// TODO: look at more tiles (radius 3)
		for (EDirection sateliteDir : EDirection.values) {
			ShortPoint2D satelitePos = sateliteDir.getNextHexPoint(super.getPos());

			if (super.isValidPosition(satelitePos) && canWorkOnPos(satelitePos)) {
				double distance = Math.hypot(satelitePos.getX() - centerPos.getX(), satelitePos.getY() - centerPos.getY());
				if (distance < bestNeighbourDistance) {
					bestNeighbourDistance = distance;
					bestNeighbourDir = sateliteDir;
				}
			}

		}
		return bestNeighbourDir;
	}

	private void executeAction(ShortPoint2D pos) {
		super.getStrategyGrid().changePlayerAt(pos, super.getPlayer());
	}

	private float getAction1Duration() {
		return 1.2f;
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
		centerPos = null;
	}

}
