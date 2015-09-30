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
package jsettlers.logic.movable.strategies.soldiers;

import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.ESoldierClass;
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
	public final ESoldierClass getSoldierClass() {
		return ESoldierClass.INFANTRY;
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
		enemy.receiveHit(getCombatStrength() * 0.1f, super.getPos(), super.getPlayer().playerId); // decrease the enemy's health
	}

	@Override
	protected short getSearchDistance(boolean isInTower) {
		return Constants.SOLDIER_SEARCH_RADIUS;
	}
}
