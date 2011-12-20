package jsettlers.logic.movable.specialists;

import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;

public class PioneerStrategy extends SpecialistStrategy {
	private static final long serialVersionUID = -796883209827059830L;

	private static final float ACTION_DURATION = 1.2f;

	public PioneerStrategy(IMovableGrid grid, Movable movable) {
		super(grid, movable);
	}

	@Override
	protected final ISPosition2D getCloseForeignTile() {
		ISPosition2D bestNeighbour = null;
		double bestNeighbourDistance = Double.MAX_VALUE; // distance from start point
		ISPosition2D centerPos = super.getCenterPos();

		// TODO: look at more tiles (radius 3)
		for (EDirection sateliteDir : EDirection.values()) {
			ISPosition2D satelitePos = sateliteDir.getNextHexPoint(super.getPos());

			if (super.getGrid().isAllowedForMovable(satelitePos.getX(), satelitePos.getY(), this) && canWorkOn(satelitePos)) {
				double distance = Math.hypot(satelitePos.getX() - centerPos.getX(), satelitePos.getY() - centerPos.getY());
				if (distance < bestNeighbourDistance) {
					bestNeighbourDistance = distance;
					bestNeighbour = satelitePos;
				}
			}

		}
		return bestNeighbour;
	}

	private boolean canWorkOn(ISPosition2D pos) {
		return super.getGrid().fitsSearchType(pos, ESearchType.FOREIGN_GROUND, this);
	}

	@Override
	protected final EMovableType getMovableType() {
		return EMovableType.PIONEER;
	}

	@Override
	protected final boolean canWorkOnCurrPos() {
		return super.getGrid().getPlayerAt(super.getPos()) != super.getPlayer() && !super.getGrid().isEnforcedByTower(super.getPos());
	}

	@Override
	protected final float getAction1Duration() {
		return ACTION_DURATION;
	}

	@Override
	protected final void executeAction() {
		super.getGrid().changePlayerAt(super.getPos(), super.getPlayer());
	}

	@Override
	protected final ESearchType getSearchType() {
		return ESearchType.FOREIGN_GROUND;
	}

	@Override
	protected float getAction2Duration() {
		return 0; // not needed
	}

	@Override
	protected boolean hasTwoActions() {
		return false;
	}

}
