package jsettlers.logic.movable.soldiers;

import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.map.newGrid.movable.IHexMovable;
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
public final class PikemanStrategy extends AbstractSoldierStrategy {
	private static final long serialVersionUID = -7925690240480881781L;

	public PikemanStrategy(IMovableGrid grid, Movable movable, EMovableType type) {
		super(grid, movable, type);
	}

	@Override
	public void executeHit(IHexMovable enemy) {
		super.setDirection(EDirection.getApproxDirection(super.getPos(), enemy.getPos()));
		super.setAction(EAction.ACTION1, 0.60f);
		enemy.hit(1.0f);
	}

	@Override
	public ESoldierType getSoldierType() {
		return ESoldierType.INFANTARY;
	}

}
