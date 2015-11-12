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

import jsettlers.algorithms.path.Path;
import jsettlers.common.buildings.OccupyerPlace;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.military.IBuildingOccupyableMovable;
import jsettlers.logic.buildings.military.IOccupyableBuilding;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;
import jsettlers.logic.movable.interfaces.AbstractStrategyGrid;
import jsettlers.logic.movable.interfaces.IAttackable;

public abstract class SoldierStrategy extends MovableStrategy implements IBuildingOccupyableMovable {
	private static final long serialVersionUID = 5246120883607071865L;

	/**
	 * Internal state of the {@link SoldierStrategy} class.
	 * 
	 * @author Andreas Eberle
	 */
	private static enum ESoldierState {
		AGGRESSIVE,

		SEARCH_FOR_ENEMIES,
		HITTING,

		INIT_GOTO_TOWER,
		GOING_TO_TOWER,
	}

	private final EMovableType movableType;

	private ESoldierState state = ESoldierState.AGGRESSIVE;
	private IOccupyableBuilding building;
	private IAttackable enemy;
	private ShortPoint2D oldPathTarget;

	private boolean inSaveGotoMode = false;

	private boolean isInTower;

	private ShortPoint2D inTowerAttackPosition;

	private boolean defending;

	public SoldierStrategy(Movable movable, EMovableType movableType) {
		super(movable);
		this.movableType = movableType;
	}

	@Override
	protected void action() {
		switch (state) {
		case AGGRESSIVE:
			break;

		case HITTING:
			hitEnemy(enemy); // after the animation, execute the actual hit.
			if (!isEnemyAttackable(enemy, isInTower)) {
				changeStateTo(ESoldierState.SEARCH_FOR_ENEMIES);
			} else {
				if (state != ESoldierState.HITTING) {
					break; // the soldier could have entered an attacked tower
				}

				if (enemy.getHealth() <= 0) {
					enemy = null;
					changeStateTo(ESoldierState.SEARCH_FOR_ENEMIES);
					break; // don't directly walk on the enemy's position, because there may be others to walk in first
				}
			}
			changeStateTo(ESoldierState.SEARCH_FOR_ENEMIES);
		case SEARCH_FOR_ENEMIES:
			final short minSearchDistance = getMinSearchDistance();
			IAttackable oldEnemy = enemy;
			enemy = super.getStrategyGrid().getEnemyInSearchArea(getAttackPosition(), super.getMovable(), minSearchDistance,
					getMaxSearchDistance(isInTower), !defending);

			// check if we have a new enemy. If so, go in unsafe mode again.
			if (oldEnemy != null && oldEnemy != enemy) {
				inSaveGotoMode = false;
			}

			// no enemy found, go back in normal mode
			if (enemy == null) {
				if (minSearchDistance > 0) {
					IAttackable toCloseEnemy = super.getStrategyGrid().getEnemyInSearchArea(
							getAttackPosition(), super.getMovable(), (short) 0, minSearchDistance, !defending);
					if (toCloseEnemy != null) {
						if (!isInTower) { // we are in danger because an enemy entered our range where we can't attack => run away
							EDirection escapeDirection = EDirection.getApproxDirection(toCloseEnemy.getPos(), getMovable().getPos());
							super.goInDirection(escapeDirection, false);
							super.getMovable().moveTo(null); // reset moveToRequest, so the soldier doesn't go there after fleeing.

						} // else { // we are in the tower, so wait and check again next time.

						break;
					}
				}
				if (defending) {
					building.towerDefended(this);
					defending = false;
				}
				changeStateTo(ESoldierState.AGGRESSIVE);

			} else if (isEnemyAttackable(enemy, isInTower)) { // if enemy is close enough, attack it
				super.lookInDirection(EDirection.getApproxDirection(super.getPos(), enemy.getPos()));
				startAttackAnimation(enemy);
				changeStateTo(ESoldierState.HITTING);

			} else if (!isInTower) {
				changeStateTo(ESoldierState.SEARCH_FOR_ENEMIES);
				goToEnemy(enemy);

			} else {
				changeStateTo(ESoldierState.SEARCH_FOR_ENEMIES);

			}

			break;

		case INIT_GOTO_TOWER:
			if (super.getPos().equals(building.getDoor()) || super.goToPos(building.getDoor())) {
				changeStateTo(ESoldierState.GOING_TO_TOWER);
			} else {
				notifyTowerThatRequestFailed();
			}
			break;

		case GOING_TO_TOWER:
			if (building.isNotDestroyed() && building.getPlayer() == super.getPlayer()) {
				OccupyerPlace place = building.addSoldier(this);
				super.setPosition(place.getPosition().calculatePoint(building.getDoor()));
				super.enableNothingToDoAction(false);
				super.setVisible(false);

				if (isBowman()) {
					this.inTowerAttackPosition = building.getTowerBowmanSearchPosition(place);
				}

				changeStateTo(ESoldierState.AGGRESSIVE);
				isInTower = true;
			} else {
				changeStateTo(ESoldierState.AGGRESSIVE); // do a check of the surrounding to find possible enemies.
				building = null;
			}
			break;
		}
	}

	private void notifyTowerThatRequestFailed() {
		if (building.getPlayer() == super.getPlayer()) { // only notify, if the tower still belongs to this player
			building.requestFailed(this.movableType);
			building = null;
			state = ESoldierState.AGGRESSIVE;
		}
	}

	protected abstract short getMaxSearchDistance(boolean isInTower);

	protected abstract short getMinSearchDistance();

	protected ShortPoint2D getAttackPosition() {
		return isInTower && isBowman() ? inTowerAttackPosition : super.getPos();
	}

	private boolean isBowman() {
		return getSoldierClass() == ESoldierClass.BOWMAN;
	}

	private void goToEnemy(IAttackable enemy) {
		if (inSaveGotoMode) {
			goToSavely(enemy);
		} else {
			ShortPoint2D pos = super.getPos();
			EDirection dir = EDirection.getApproxDirection(pos, enemy.getPos());

			if (super.goInDirection(dir, false)) {
				return;
			} else {
				inSaveGotoMode = true;
				goToSavely(enemy);
			}
		}
	}

	private void goToSavely(IAttackable enemy) {
		super.goToPos(enemy.getPos());
	}

	private void changeStateTo(ESoldierState state) {
		this.state = state;
		switch (state) {
		case AGGRESSIVE:
			if (oldPathTarget != null) {
				super.goToPos(oldPathTarget);
				oldPathTarget = null;
			}
			break;

		default:
			break;
		}
	}

	protected abstract void hitEnemy(IAttackable enemy);

	protected abstract void startAttackAnimation(IAttackable enemy);

	protected abstract boolean isEnemyAttackable(IAttackable enemy, boolean isInTower);

	@Override
	public boolean setOccupyableBuilding(IOccupyableBuilding building) {
		if (state != ESoldierState.GOING_TO_TOWER && state != ESoldierState.INIT_GOTO_TOWER) {
			this.building = building;
			changeStateTo(ESoldierState.INIT_GOTO_TOWER);
			super.abortPath();
			this.oldPathTarget = null; // this prevents that the soldiers go to this position after he leaves the tower.
			return true;
		} else {
			return false;
		}
	}

	@Override
	public EMovableType getMovableType() {
		return movableType;
	}

	@Override
	public Movable getMovable() {
		return super.getMovable();
	}

	@Override
	public void leaveOccupyableBuilding(ShortPoint2D newPosition) {
		super.setPosition(newPosition);
		super.enableNothingToDoAction(true);
		super.setVisible(true);

		isInTower = false;
		changeStateTo(ESoldierState.SEARCH_FOR_ENEMIES);
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
	}

	@Override
	public void informAboutAttackable(IAttackable other) {
		if (state == ESoldierState.AGGRESSIVE && (!isInTower || getSoldierClass() == ESoldierClass.BOWMAN)) {
			changeStateTo(ESoldierState.SEARCH_FOR_ENEMIES); // this searches for the enemy on the next timer click
		}
	}

	@Override
	public void setDefendingAt(ShortPoint2D pos) {
		super.setPosition(pos);
		changeStateTo(ESoldierState.SEARCH_FOR_ENEMIES);
		defending = true;
	}

	@Override
	protected boolean checkPathStepPreconditions(ShortPoint2D pathTarget, int step) {
		boolean result = !((state == ESoldierState.SEARCH_FOR_ENEMIES || state == ESoldierState.HITTING) && step >= 2);
		if (!result && oldPathTarget == null) {
			oldPathTarget = pathTarget;
		}

		if (state == ESoldierState.GOING_TO_TOWER && (!building.isNotDestroyed() || building.getPlayer() != super.getPlayer())) {
			result = false;
		}

		if (enemy != null && state == ESoldierState.SEARCH_FOR_ENEMIES && isEnemyAttackable(enemy, false)) {
			result = false;
		}

		return result;
	}

	@Override
	protected void moveToPathSet(ShortPoint2D oldPosition, ShortPoint2D oldTargetPos, ShortPoint2D targetPos) {
		if (targetPos != null && this.oldPathTarget != null) {
			oldPathTarget = null; // reset the path target to be able to get the new one when we hijack the path
			inSaveGotoMode = false;
		}
		changeStateTo(ESoldierState.SEARCH_FOR_ENEMIES);
	}

	@Override
	protected boolean isMoveToAble() {
		return state != ESoldierState.INIT_GOTO_TOWER && state != ESoldierState.GOING_TO_TOWER && !isInTower;
	}

	@Override
	protected Path findWayAroundObstacle(EDirection direction, ShortPoint2D position, Path path) {
		if (state == ESoldierState.SEARCH_FOR_ENEMIES) {
			AbstractStrategyGrid grid = super.getStrategyGrid();
			EDirection leftDir = direction.getNeighbor(-1);
			ShortPoint2D leftPos = leftDir.getNextHexPoint(position);
			EDirection rightDir = direction.getNeighbor(1);
			ShortPoint2D rightPos = rightDir.getNextHexPoint(position);

			if (grid.isFreePosition(leftPos)) {
				return new Path(leftPos);
			} else if (grid.isFreePosition(rightPos)) {
				return new Path(rightPos);
			} else {
				EDirection twoLeftDir = direction.getNeighbor(-2);
				ShortPoint2D twoLeftPos = twoLeftDir.getNextHexPoint(position);
				EDirection twoRightDir = direction.getNeighbor(2);
				ShortPoint2D twoRightPos = twoRightDir.getNextHexPoint(position);

				if (grid.isFreePosition(twoLeftPos)) {
					return new Path(twoLeftPos);
				} else if (grid.isFreePosition(twoRightPos)) {
					return new Path(twoRightPos);
				} else {
					return path;
				}
			}
		} else {
			return super.findWayAroundObstacle(direction, position, path);
		}
	}

	@Override
	protected void strategyKilledEvent(ShortPoint2D pathTarget) {
		if (building != null) {
			if (isInTower) {
				building.removeSoldier(this);
			} else {
				notifyTowerThatRequestFailed();
			}
		}
	}

	@Override
	protected void pathAborted(ShortPoint2D pathTarget) {
		state = ESoldierState.AGGRESSIVE;
	}

	protected float getCombatStrength() {
		return super.getPlayer().getCombatStrengthInformation().getCombatStrength(isOnOwnGround());
	}
}
