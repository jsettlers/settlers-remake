package jsettlers.logic.movable.strategies.soldiers;

import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.interfaces.IAttackable;

/**
 * Strategy for swordsman and pikeman {@link Movable}s.
 * 
 * @author Andreas Eberle
 * 
 */
public final class InfantryStrategy extends SoldierStrategy {
	private static final long serialVersionUID = -2367165698305111060L;
	private static final float INFANTRY_ATTACK_DURATION = 1;

	public InfantryStrategy(Movable movable, EMovableType movableType) {
		super(movable, movableType);
	}

	@Override
	public final ESoldierType getSoldierType() {
		return ESoldierType.INFANTRY;
	}

	@Override
	protected boolean isEnemyAttackable(IAttackable enemy, boolean isInTower) {
		return EDirection.getDirection(super.getPos(), enemy.getPos()) != null;
	}

	@Override
	protected void startAttackAnimation(IAttackable enemy) {
		super.playAction(EAction.ACTION1, INFANTRY_ATTACK_DURATION);
	}

	@Override
	protected void hitEnemy(IAttackable enemy) {
		enemy.receiveHit(0.1f, super.getPos(), super.getPlayer().playerId); // decrease the enemy's health
	}

	@Override
	protected short getSearchDistance(boolean isInTower) {
		return Constants.SOLDIER_SEARCH_RADIUS;
	}
}
