package jsettlers.logic.movable.soldiers;

import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;

/**
 * strategy for a swordsman.
 * <p />
 * needs to follow movables and paths given by the user.
 * 
 * @author Andreas Eberle
 * 
 */
public class PikemanStrategy extends AbstractSoldierStrategy {
	private static final long serialVersionUID = -7925690240480881781L;

	public PikemanStrategy(IMovableGrid grid, Movable movable, EMovableType type) {
		super(grid, movable, type);
	}

	@Override
	protected short getSearchRadius() {
		return Constants.PIKEMAN_SEARCH_RADIUS;
	}

	@Override
	protected void executeHit(ISPosition2D enemyPos) {
		EDirection enemyDir = EDirection.getDirection(super.getPos(), enemyPos);
		super.setDirection(enemyDir);
		super.setAction(EAction.ACTION1, 0.60f);
		super.getGrid().getMovable(enemyPos).hit(1.0f);
	}

	@Override
	protected boolean canHit(ISPosition2D enemyPos) {
		return EDirection.getDirection(this.getPos(), enemyPos) != null;
	}

	@Override
	public ESoldierType getSoldierType() {
		return ESoldierType.INFANTARY;
	}

}
