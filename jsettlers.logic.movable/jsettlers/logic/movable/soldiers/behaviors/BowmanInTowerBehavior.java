package jsettlers.logic.movable.soldiers.behaviors;

import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.buildings.military.IOccupyableBuilding;

public final class BowmanInTowerBehavior extends InfantryInTowerBehavior implements IFightingBehaviorUser {
	private static final long serialVersionUID = -6415950179506334531L;
	private static final short BOWMAN_IN_TOWER_SEARCH_RADIUS = 32;

	BowmanInTowerBehavior(ISoldierBehaviorable soldier, IOccupyableBuilding building) {
		super(soldier, building);
	}

	private int delayCtr = 0;
	private boolean enemyFoundLastTime = false;

	@Override
	public SoldierBehavior calculate(ISPosition2D pos, IPathCalculateable pathCalcable) {
		super.calculate(pos, pathCalcable);

		if (enemyFoundLastTime || delayCtr > WatchingBehavior.DELAY) {
			delayCtr = 0;

			// TODO for better performance: only check the enemy positions and outer circle
			Path path = super.getGrid().getDijkstra()
					.find(pathCalcable, pos.getX(), pos.getY(), (short) 1, BOWMAN_IN_TOWER_SEARCH_RADIUS, ESearchType.ENEMY);
			if (path != null) {
				enemyFoundLastTime = true;
				if (canHit(pos, path.getTargetPos())) {
					return new FightingBehavior(getSoldier(), path.getTargetPos(), this);
				} else {
					// do nothing
					return this;
				}
			} else {
				enemyFoundLastTime = false;
				return this;
			}
		} else {
			delayCtr++;
			return this;
		}
	}

	private final boolean canHit(ISPosition2D pos, ISPosition2D enemyPos) { // TODO use MapCircle to get a circle on the map
		return Math.hypot(pos.getX() - enemyPos.getX(), pos.getY() - enemyPos.getY()) <= BOWMAN_IN_TOWER_SEARCH_RADIUS;
	}

	@Override
	public SoldierBehavior getFinishedBehavior() {
		return this;
	}

	@Override
	public void setFinishedMovableAction() {
		super.soldier.setDontMove(true);
	}
}
