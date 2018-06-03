/*******************************************************************************
 * Copyright (c) 2015, 2016
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

import jsettlers.algorithms.path.Path;
import jsettlers.common.buildings.OccupierPlace;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.military.IBuildingOccupyableMovable;
import jsettlers.logic.buildings.military.occupying.IOccupyableBuilding;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.EGoInDirectionMode;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;
import jsettlers.logic.movable.interfaces.IAttackable;

public abstract class SoldierStrategy extends MovableStrategy implements IBuildingOccupyableMovable {
	private static final long serialVersionUID = 5246120883607071865L;

	/**
	 * Internal state of the {@link SoldierStrategy} class.
	 * 
	 * @author Andreas Eberle
	 */
	private enum ESoldierState {
		AGGRESSIVE,

		SEARCH_FOR_ENEMIES,
		HITTING,

		INIT_GOTO_TOWER,
		GOING_TO_TOWER,
	}

	private final EMovableType movableType;

	private ESoldierState state = ESoldierState.SEARCH_FOR_ENEMIES;
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
			if (!isEnemyAttackable(enemy, isInTower)) {
				changeStateTo(ESoldierState.SEARCH_FOR_ENEMIES);
			} else {
				hitEnemy(enemy); // after the animation, execute the actual hit.

				if (state != ESoldierState.HITTING) {
					break; // the soldier could have entered an attacked tower
				}

				if (!enemy.isAlive()) {
					enemy = null;
					changeStateTo(ESoldierState.SEARCH_FOR_ENEMIES);
					break; // don't directly walk on the enemy's position, because there may be others to walk in first
				}
			}
			changeStateTo(ESoldierState.SEARCH_FOR_ENEMIES);
		case SEARCH_FOR_ENEMIES:
			final short minSearchDistance = getMinSearchDistance();
			IAttackable oldEnemy = enemy;
			enemy = super.getGrid().getEnemyInSearchArea(getAttackPosition(), movable, minSearchDistance, getMaxSearchDistance(isInTower), !defending);

			// check if we have a new enemy. If so, go in unsafe mode again.
			if (oldEnemy != null && oldEnemy != enemy) {
				inSaveGotoMode = false;
			}

			// no enemy found, go back in normal mode
			if (enemy == null) {
				if (minSearchDistance > 0) {
					IAttackable toCloseEnemy = super.getGrid().getEnemyInSearchArea(
							getAttackPosition(), movable, (short) 0, minSearchDistance, !defending);
					if (toCloseEnemy != null) {
						if (!isInTower) { // we are in danger because an enemy entered our range where we can't attack => run away
							EDirection escapeDirection = EDirection.getApproxDirection(toCloseEnemy.getPosition(), movable.getPosition());
							super.goInDirection(escapeDirection, EGoInDirectionMode.GO_IF_ALLOWED_AND_FREE);
							movable.moveTo(null); // reset moveToRequest, so the soldier doesn't go there after fleeing.

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
				super.lookInDirection(EDirection.getApproxDirection(movable.getPosition(), enemy.getPosition()));
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
			changeStateTo(ESoldierState.GOING_TO_TOWER); // change state before requesting path because of checkPathStepPreconditions()
			if (!movable.getPosition().equals(building.getDoor()) && !super.goToPos(building.getDoor())) {
				notifyTowerThatRequestFailed();
			}
			break;

		case GOING_TO_TOWER:
			if (!building.isDestroyed() && building.getPlayer() == movable.getPlayer()) {
				OccupierPlace place = building.addSoldier(this);
				super.setVisible(false);
				super.setPosition(place.getPosition().calculatePoint(building.getPosition()));
				super.enableNothingToDoAction(false);

				if (isBowman()) {
					this.inTowerAttackPosition = building.getTowerBowmanSearchPosition(place);
					changeStateTo(ESoldierState.SEARCH_FOR_ENEMIES);
				} else {
					changeStateTo(ESoldierState.AGGRESSIVE);
				}

				isInTower = true;
			} else {
				changeStateTo(ESoldierState.AGGRESSIVE); // do a check of the surrounding to find possible enemies.
				building = null;
			}
			break;
		}
	}

	private void notifyTowerThatRequestFailed() {
		if (building.getPlayer() == movable.getPlayer()) { // only notify, if the tower still belongs to this player
			building.requestFailed(this);
			building = null;
			state = ESoldierState.AGGRESSIVE;
		}
	}

	protected abstract short getMaxSearchDistance(boolean isInTower);

	protected abstract short getMinSearchDistance();

	protected ShortPoint2D getAttackPosition() {
		return isInTower && isBowman() ? inTowerAttackPosition : movable.getPosition();
	}

	private boolean isBowman() {
		return getSoldierClass() == ESoldierClass.BOWMAN;
	}

	private void goToEnemy(IAttackable enemy) {
		if (inSaveGotoMode) {
			goToSavely(enemy);
		} else {
			ShortPoint2D pos = movable.getPosition();
			EDirection dir = EDirection.getApproxDirection(pos, enemy.getPosition());

			if (super.goInDirection(dir, EGoInDirectionMode.GO_IF_ALLOWED_AND_FREE)) {
				return;
			} else {
				inSaveGotoMode = true;
				goToSavely(enemy);
			}
		}
	}

	private void goToSavely(IAttackable enemy) {
		super.goToPos(enemy.getPosition());
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

	public IBuildingOccupyableMovable setOccupyableBuilding(IOccupyableBuilding building) {
		if (state != ESoldierState.GOING_TO_TOWER && state != ESoldierState.INIT_GOTO_TOWER) {
			this.building = building;
			changeStateTo(ESoldierState.INIT_GOTO_TOWER);
			super.abortPath();
			this.oldPathTarget = null; // this prevents that the soldiers go to this position after he leaves the tower.
			return this;
		} else {
			return null;
		}
	}

	@Override
	public EMovableType getMovableType() {
		return movableType;
	}

	@Override
	public Movable getMovable() {
		return movable;
	}

	@Override
	public void leaveOccupyableBuilding(ShortPoint2D newPosition) {
		if (isInTower) {
			super.setPosition(newPosition);
			super.enableNothingToDoAction(true);
			super.setVisible(true);
			super.movable.setSelected(false);

			isInTower = false;
			building = null;
			defending = false;
			changeStateTo(ESoldierState.SEARCH_FOR_ENEMIES);

		} else if (state == ESoldierState.INIT_GOTO_TOWER || state == ESoldierState.GOING_TO_TOWER) {
			super.abortPath();
			building = null;
			changeStateTo(ESoldierState.SEARCH_FOR_ENEMIES);
		}
	}

	@Override
	public void setSelected(boolean selected) {
		movable.setSelected(selected);
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
		if (state == ESoldierState.INIT_GOTO_TOWER) {
			return false; // abort previous path when we just got a tower set
		}

		boolean result = !((state == ESoldierState.SEARCH_FOR_ENEMIES || state == ESoldierState.HITTING) && step >= 2);
		if (!result && oldPathTarget == null) {
			oldPathTarget = pathTarget;
		}

		if (state == ESoldierState.GOING_TO_TOWER && (building == null || building.isDestroyed() || building.getPlayer() != movable.getPlayer())) {
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
	protected boolean canBeControlledByPlayer() {
		return state != ESoldierState.INIT_GOTO_TOWER && state != ESoldierState.GOING_TO_TOWER && !isInTower;
	}

	@Override
	protected Path findWayAroundObstacle(ShortPoint2D position, Path path) {
		if (state == ESoldierState.SEARCH_FOR_ENEMIES) {
			EDirection direction = EDirection.getDirection(position, path.getNextPos());

			EDirection rightDir = direction.getNeighbor(-1);
			ShortPoint2D rightPos = rightDir.getNextHexPoint(position);
			EDirection leftDir = direction.getNeighbor(1);
			ShortPoint2D leftPos = leftDir.getNextHexPoint(position);

			ShortPoint2D freePosition = getRandomFreePosition(rightPos, leftPos);

			if (freePosition != null) {
				return new Path(freePosition);

			} else {
				EDirection twoRightDir = direction.getNeighbor(-2);
				ShortPoint2D twoRightPos = twoRightDir.getNextHexPoint(position);
				EDirection twoLeftDir = direction.getNeighbor(2);
				ShortPoint2D twoLeftPos = twoLeftDir.getNextHexPoint(position);

				freePosition = getRandomFreePosition(twoRightPos, twoLeftPos);

				if (freePosition != null) {
					return new Path(freePosition);
				} else {
					return path;
				}
			}
		} else {
			return super.findWayAroundObstacle(position, path);
		}
	}

	private ShortPoint2D getRandomFreePosition(ShortPoint2D pos1, ShortPoint2D pos2) {
		boolean pos1Free = getGrid().isFreePosition(pos1.x, pos1.y);
		boolean pos2Free = getGrid().isFreePosition(pos2.x, pos2.y);

		if (pos1Free && pos2Free) {
			return MatchConstants.random().nextBoolean() ? pos1 : pos2;
		} else if (pos1Free) {
			return pos1;
		} else if (pos2Free) {
			return pos2;
		} else {
			return null;
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
		switch (state) {
		case INIT_GOTO_TOWER:
		case GOING_TO_TOWER:
			notifyTowerThatRequestFailed();
			break;
		default:
			state = ESoldierState.AGGRESSIVE;
			break;
		}
	}

	protected float getCombatStrength() {
		return movable.getPlayer().getCombatStrengthInformation().getCombatStrength(isOnOwnGround());
	}
}
