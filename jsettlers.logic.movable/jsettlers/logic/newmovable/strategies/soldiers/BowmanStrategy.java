package jsettlers.logic.newmovable.strategies.soldiers;

import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.newmovable.NewMovable;

public final class BowmanStrategy extends SoldierStrategy {
	private static final long serialVersionUID = 7062243467280721040L;
	private static final float BOWMAN_ATTACK_DURATION = 0.9f;
	private static final int SQUARE_BOWMAN_ATTACK_RADIUS = Constants.BOWMAN_ATTACK_RADIUS * Constants.BOWMAN_ATTACK_RADIUS;

	public BowmanStrategy(NewMovable movable, EMovableType movableType) {
		super(movable, movableType);
	}

	@Override
	public ESoldierType getSoldierType() {
		return ESoldierType.BOWMAN;
	}

	@Override
	protected boolean isEnemyAttackable(NewMovable enemy) {
		ShortPoint2D pos = super.getPos();
		ShortPoint2D enemyPos = enemy.getPos();

		final int dx = Math.abs(pos.getX() - enemyPos.getX());
		final int dy = Math.abs(pos.getY() - enemyPos.getY());

		return dx * dx + dy * dy <= SQUARE_BOWMAN_ATTACK_RADIUS;
	}

	@Override
	protected void startAttackAnimation(NewMovable enemy) {
		super.playAction(EAction.ACTION1, BOWMAN_ATTACK_DURATION);

		super.getStrategyGrid().addArrowObject(enemy.getPos(), super.getPos(), 0.7f);
	}

	@Override
	protected void hitEnemy(NewMovable enemy) {
	}
}
