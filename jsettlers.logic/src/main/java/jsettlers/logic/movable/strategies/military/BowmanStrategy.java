/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.movable.strategies.military;

import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.interfaces.IAttackable;

/**
 * Strategy of a bowman.
 * 
 * @author Andreas Eberle
 * 
 */
public final class BowmanStrategy extends SoldierStrategy {
	private static final long serialVersionUID = 7062243467280721040L;
	private static final float BOWMAN_ATTACK_DURATION = 1.2f;

	public BowmanStrategy(Movable movable, EMovableType movableType) {
		super(movable, movableType);
	}

	@Override
	public final ESoldierClass getSoldierClass() {
		return ESoldierClass.BOWMAN;
	}

	@Override
	protected boolean isEnemyAttackable(IAttackable enemy, boolean isInTower) {
		if (!enemy.isAlive()){
			return false;
		}

		ShortPoint2D pos = getAttackPosition();
		ShortPoint2D enemyPos = enemy.getPosition();

		int distance = pos.getOnGridDistTo(enemyPos);

		if (isInTower) {
			return Constants.BOWMAN_MIN_ATTACK_DISTANCE <= distance && distance <= Constants.BOWMAN_IN_TOWER_ATTACK_RADIUS;
		} else {
			return Constants.BOWMAN_MIN_ATTACK_DISTANCE <= distance && distance <= Constants.BOWMAN_ATTACK_RADIUS;
		}
	}

	@Override
	protected void startAttackAnimation(IAttackable enemy) {
		super.playAction(EMovableAction.ACTION1, BOWMAN_ATTACK_DURATION);
		super.getGrid().addArrowObject(enemy.getPosition(), movable.getPosition(), movable.getPlayer().playerId,
				getMovableType().getStrength() * getCombatStrength());
	}

	@Override
	protected void hitEnemy(IAttackable enemy) {
	}

	@Override
	protected short getMaxSearchDistance(boolean isInTower) {
		return isInTower ? Constants.TOWER_ATTACKABLE_SEARCH_RADIUS : Constants.SOLDIER_SEARCH_RADIUS;
	}

	@Override
	protected short getMinSearchDistance() {
		return Constants.BOWMAN_MIN_ATTACK_DISTANCE;
	}
}
