package jsettlers.logic.movable.soldiers;

import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.map.newGrid.movable.IHexMovable;
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
public final class BowmanStrategy extends AbstractSoldierStrategy {
	private static final long serialVersionUID = 4101130971112016217L;

	public BowmanStrategy(IMovableGrid grid, Movable movable, EMovableType type) {
		super(grid, movable, type);
	}

	@Override
	public void executeHit(IHexMovable enemy) {
		super.setAction(EAction.ACTION1, 0.8f);

		super.setDirection(EDirection.getApproxDirection(super.getPos(), enemy.getPos()));

		super.getGrid().getMapObjectsManager().addArrowObject(enemy, super.getPos(), 0.8f);
	}

	@Override
	public final ESoldierType getSoldierType() {
		return ESoldierType.BOWMAN;
	}

}
