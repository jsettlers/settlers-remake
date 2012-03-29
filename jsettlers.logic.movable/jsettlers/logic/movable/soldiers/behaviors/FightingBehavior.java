package jsettlers.logic.movable.soldiers.behaviors;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.map.newGrid.movable.IHexMovable;

/**
 * This behavior causes the soldier to kill fight an enemy at a given position until it is dead or no longer there.
 * 
 * @author Andreas Eberle
 * 
 */
final class FightingBehavior extends SoldierBehavior {
	private static final long serialVersionUID = -7277998697464430659L;

	private final ShortPoint2D enemyPos;

	private final IFightingBehaviorUser user;

	FightingBehavior(ISoldierBehaviorable soldier, ShortPoint2D enemyPos, IFightingBehaviorUser user) {
		super(soldier);
		this.enemyPos = enemyPos;
		this.user = user;
	}

	@Override
	public SoldierBehavior calculate(ShortPoint2D pos, IPathCalculateable pathCalcable) {
		IHexMovable enemy = super.getGrid().getMovable(enemyPos);
		if (enemy != null && enemy.getPlayer() != super.getPlayer()) {
			super.getSoldier().executeHit(enemy);
			return this;
		} else {
			user.setFinishedMovableAction();
			return user.getFinishedBehavior();
		}
	}

	@Override
	public void pathRequestFailed() {
		System.out.println("should not happen here: FightingBehavior.pathRequestFailed()");
	}

}
