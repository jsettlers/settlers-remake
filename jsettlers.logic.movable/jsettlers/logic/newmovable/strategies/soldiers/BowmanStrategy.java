package jsettlers.logic.newmovable.strategies.soldiers;

import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.newmovable.interfaces.IAttackable;

/**
 * Strategy of a bowman.
 * 
 * @author Andreas Eberle
 * 
 */
public final class BowmanStrategy extends SoldierStrategy {
	private static final long serialVersionUID = 7062243467280721040L;
	private static final float BOWMAN_ATTACK_DURATION = 1.2f;
	private static final int SQUARE_BOWMAN_ATTACK_RADIUS = Constants.BOWMAN_ATTACK_RADIUS * Constants.BOWMAN_ATTACK_RADIUS;

	// private static final int SQAURE_BOWMAN_MINIMUM_DISTANCE = Constants.BOWMAN_MIN_ATTACK_DISTANCE * Constants.BOWMAN_MIN_ATTACK_DISTANCE;

	public BowmanStrategy(NewMovable movable, EMovableType movableType) {
		super(movable, movableType);
	}

	@Override
	public final ESoldierType getSoldierType() {
		return ESoldierType.BOWMAN;
	}

	@Override
	protected boolean isEnemyAttackable(IAttackable enemy) {
		ShortPoint2D pos = super.getPos();
		ShortPoint2D enemyPos = enemy.getPos();

		final int dx = Math.abs(pos.getX() - enemyPos.getX());
		final int dy = Math.abs(pos.getY() - enemyPos.getY());

		final int squareDist = dx * dx + dy * dy;

		// SQAURE_BOWMAN_MINIMUM_DISTANCE <= squareDist &&
		return squareDist <= SQUARE_BOWMAN_ATTACK_RADIUS;
	}

	@Override
	protected void startAttackAnimation(IAttackable enemy) {
		super.playAction(EAction.ACTION1, BOWMAN_ATTACK_DURATION);

		super.getStrategyGrid().addArrowObject(enemy.getPos(), super.getPos(), super.getPlayer(), 0.08f);
	}

	@Override
	protected void hitEnemy(IAttackable enemy) {
	}

	@Override
	protected short getSearchDistance(boolean isInTower) {
		return isInTower ? Constants.TOWER_SEARCH_RADIUS : Constants.BOWMAN_ATTACK_RADIUS;
	}
}
