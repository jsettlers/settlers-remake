package jsettlers.logic.newmovable.strategies.soldiers;

import jsettlers.common.buildings.OccupyerPlace;
import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.buildings.military.IBuildingOccupyableMovable;
import jsettlers.logic.buildings.military.IOccupyableBuilding;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.newmovable.NewMovableStrategy;
import jsettlers.logic.newmovable.interfaces.IAttackable;
import jsettlers.logic.newmovable.interfaces.IStrategyGrid;

public abstract class SoldierStrategy extends NewMovableStrategy implements IBuildingOccupyableMovable {
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

	public SoldierStrategy(NewMovable movable, EMovableType movableType) {
		super(movable);
		this.movableType = movableType;
	}

	@Override
	protected void action() {
		switch (state) {
		case AGGRESSIVE:
			break;

		case HITTING:
			hitEnemy(enemy);
			if (state != ESoldierState.HITTING) {
				break; // the soldier could have entered an attacked tower
			}

			if (enemy.getHealth() <= 0) {
				enemy = null;
				changeStateTo(ESoldierState.SEARCH_FOR_ENEMIES);
				break; // don't directly walk on the enemy's position, because there may be others to walk in first
			}
		case SEARCH_FOR_ENEMIES:
			IAttackable oldEnemy = enemy;
			enemy = super.getStrategyGrid().getEnemyInSearchArea(getAttackPosition(), super.getMovable(), getSearchDistance(isInTower));

			// check if we have a new enemy. If so, go in unsave mode again.
			if (oldEnemy != null && oldEnemy != enemy) {
				inSaveGotoMode = false;
			}

			// no enemy found, go back in normal mode
			if (enemy == null) {
				if (defending) {
					building.towerDefended(this);
					defending = false;
				}
				changeStateTo(ESoldierState.AGGRESSIVE);
				break;
			}

			if (isEnemyAttackable(enemy, isInTower)) { // if enemy is close enough, attack it
				super.lookInDirection(EDirection.getApproxDirection(super.getPos(), enemy.getPos()));
				startAttackAnimation(enemy);
				changeStateTo(ESoldierState.HITTING);
			} else if (!isInTower) {
				changeStateTo(ESoldierState.SEARCH_FOR_ENEMIES);
				goToEnemy(enemy);
			}

			break;

		case INIT_GOTO_TOWER:
			super.goToPos(building.getDoor());
			changeStateTo(ESoldierState.GOING_TO_TOWER);
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
				changeStateTo(ESoldierState.SEARCH_FOR_ENEMIES); // do a check of the surrounding to find possible enemies.
			}
			break;

		}
	}

	protected abstract short getSearchDistance(boolean isInTower);

	private ShortPoint2D getAttackPosition() {
		return isInTower && isBowman() ? inTowerAttackPosition : super.getPos();
	}

	private boolean isBowman() {
		return getSoldierType() == ESoldierType.BOWMAN;
	}

	private void goToEnemy(IAttackable enemy) {
		if (inSaveGotoMode) {
			goToSavely(enemy);
		} else {
			ShortPoint2D pos = super.getPos();
			EDirection dir = EDirection.getApproxDirection(pos, enemy.getPos());

			ShortPoint2D nextPos = dir.getNextHexPoint(pos);
			if (super.isValidPosition(nextPos)) {
				super.forceGoInDirection(dir);
				return;
			} else {
				goToSavely(enemy);
				inSaveGotoMode = true;
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
	public void setOccupyableBuilding(IOccupyableBuilding building) {
		this.building = building;
		changeStateTo(ESoldierState.INIT_GOTO_TOWER);
		super.abortPath();
		this.oldPathTarget = null; // this prevents that the soldiers go to this position after he leaves the tower.
	}

	@Override
	public EMovableType getMovableType() {
		return movableType;
	}

	@Override
	public NewMovable getMovable() {
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
		if (state == ESoldierState.AGGRESSIVE && (!isInTower || getSoldierType() == ESoldierType.BOWMAN)) {
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

		return result;
	}

	@Override
	protected void moveToPathSet(ShortPoint2D oldPosition, ShortPoint2D oldTargetPos, ShortPoint2D targetPos) {
		if (targetPos != null && this.oldPathTarget != null) {
			oldPathTarget = null; // reset the path target to be able to get the new one when we hijack the path
			inSaveGotoMode = false;
		}
	}

	@Override
	protected boolean isMoveToAble() {
		return state != ESoldierState.INIT_GOTO_TOWER && state != ESoldierState.GOING_TO_TOWER && !isInTower;
	}

	@Override
	protected Path findWayAroundObstacle(EDirection direction, ShortPoint2D position, Path path) {
		if (state == ESoldierState.SEARCH_FOR_ENEMIES) {
			IStrategyGrid grid = super.getStrategyGrid();
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
				building.requestFailed(movableType);
			}
		}
	}
}
