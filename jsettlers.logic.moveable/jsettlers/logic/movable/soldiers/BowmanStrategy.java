package jsettlers.logic.movable.soldiers;

import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;

/**
 * strategy for a bowman.
 * <p />
 * needs to follow movables and paths given by the user.
 * 
 * @author Andreas Eberle
 * 
 */
public class BowmanStrategy extends AbstractSoldierStrategy {
	public BowmanStrategy(IMovableGrid grid, Movable movable, EMovableType type) {
		super(grid, movable, type);
	}

	@Override
	protected short getSearchRadius() {
		return Constants.BOWMAN_SEARCH_RADIUS;
	}

	@Override
	protected void executeHit(ISPosition2D enemyPos) {
		super.setAction(EAction.ACTION1, 0.8f);
		EDirection dir = EDirection.getApproxDirection(super.getPos(), enemyPos);
		if (dir != null)
			super.setDirection(dir);

		super.getGrid().getMapObjectsManager().addArrowObject(super.getGrid().getMovable(enemyPos), super.getPos(), 0.8f);
	}

	@Override
	protected boolean canHit(ISPosition2D enemyPos) {
		return Math.hypot(super.getPos().getX() - enemyPos.getX(), super.getPos().getY() - enemyPos.getY()) <= Constants.BOWMAN_FIRE_RADIUS;
	}

}
