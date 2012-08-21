package jsettlers.logic.newmovable.strategies.soldiers;

import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.newmovable.NewMovable;

/**
 * Strategy for swordsman and pikeman {@link NewMovable}s.
 * 
 * @author Andreas Eberle
 * 
 */
public final class InfantryStrategy extends SoldierStrategy {
	private static final long serialVersionUID = -2367165698305111060L;
	private static final float INFANTRY_ATTACK_DURATION = 1;

	public InfantryStrategy(NewMovable movable, EMovableType movableType) {
		super(movable, movableType);
	}

	@Override
	public ESoldierType getSoldierType() {
		return ESoldierType.INFANTRY;
	}

	@Override
	protected boolean isEnemyAttackable(NewMovable enemy) {
		return super.getPos().getOnGridDistTo(enemy.getPos()) == 1;
	}

	@Override
	protected void startAttackAnimation(NewMovable enemy) {
		super.playAction(EAction.ACTION1, INFANTRY_ATTACK_DURATION);
	}

	@Override
	protected void hitEnemy(NewMovable enemy) {
		enemy.hit(0.1f); // decrease the enemy's health
	}

}
