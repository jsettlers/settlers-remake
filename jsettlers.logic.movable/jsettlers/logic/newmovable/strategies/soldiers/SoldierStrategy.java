package jsettlers.logic.newmovable.strategies.soldiers;

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
	private final EMovableType movableType;

	private ESoldierState state = ESoldierState.AGGRESSIVE;
	private IOccupyableBuilding building;
	private IAttackable enemy;
	private ShortPoint2D oldPathTarget;

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
			if (enemy.getHealth() <= 0) {
				enemy = null;
				state = ESoldierState.ENEMY_FOUND;
				break; // don't directly walk on the enemy's position, because there may be others to walk in first
			}
		case ENEMY_FOUND:
			enemy = super.getStrategyGrid().getEnemyInSearchArea(super.getMovable());
			if (enemy == null) { // no enemy found, go back in normal mode
				changeStateTo(ESoldierState.AGGRESSIVE);
				break;
			}

			if (isEnemyAttackable(enemy)) { // if enemy is close enough, attack it
				super.lookInDirection(EDirection.getApproxDirection(super.getPos(), enemy.getPos()));
				startAttackAnimation(enemy);
				state = ESoldierState.HITTING;
			} else {
				state = ESoldierState.ENEMY_FOUND;
				goToEnemy(enemy);
			}

			break;

		case INIT_GOTO_TOWER:
			super.goToPos(building.getDoor());
			state = ESoldierState.GOING_TO_TOWER;
			break;

		case GOING_TO_TOWER:
			ShortPoint2D pos = building.setSoldier(this);
			super.setPosition(pos);
			super.enableNothingToDoAction(false);
			super.setVisible(false);
			state = ESoldierState.IN_TOWER;
			break;

		case IN_TOWER:
			break;
		}
	}

	private void goToEnemy(IAttackable enemy) {
		// ShortPoint2D pos = super.getPos();
		// EDirection dir = EDirection.getApproxDirection(pos, enemy.getPos());
		//
		// super.forceGoInDirection(dir);

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
		}
	}

	protected abstract void hitEnemy(IAttackable enemy);

	protected abstract void startAttackAnimation(IAttackable enemy);

	protected abstract boolean isEnemyAttackable(IAttackable enemy);

	@Override
	public void setOccupyableBuilding(IOccupyableBuilding building) {
		this.building = building;
		this.state = ESoldierState.INIT_GOTO_TOWER;
		super.abortPath();
	}

	/**
	 * Internal state of the {@link SoldierStrategy} class.
	 * 
	 * @author Andreas Eberle
	 */
	private static enum ESoldierState {
		AGGRESSIVE,

		ENEMY_FOUND,
		HITTING,

		INIT_GOTO_TOWER,
		GOING_TO_TOWER,
		IN_TOWER,
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

		state = ESoldierState.ENEMY_FOUND;
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
	}

	@Override
	protected void informAboutAttackable(IAttackable other) {
		if (state == ESoldierState.AGGRESSIVE) {
			state = ESoldierState.ENEMY_FOUND; // this searches for the enemy on the next timer click
		}
	}

	@Override
	protected boolean checkPathStepPreconditions(ShortPoint2D pathTarget, int step) {
		boolean result = !((state == ESoldierState.ENEMY_FOUND || state == ESoldierState.HITTING) && step >= 2);
		if (!result && oldPathTarget == null) {
			oldPathTarget = pathTarget;
		}

		return result;
	}

	@Override
	protected void moveToPathSet(ShortPoint2D oldTargetPos, ShortPoint2D targetPos) {
		if (targetPos != null && this.oldPathTarget != null) {
			oldPathTarget = null; // reset the path target to be able to get the new one when we hijack the path
		}
	}

	@Override
	protected boolean isMoveToAble() {
		return state != ESoldierState.INIT_GOTO_TOWER && state != ESoldierState.GOING_TO_TOWER && state != ESoldierState.IN_TOWER;
	}

	@Override
	protected Path findWayAroundObstacle(EDirection direction, ShortPoint2D position, Path path) {
		if (state == ESoldierState.ENEMY_FOUND) {
			IStrategyGrid grid = super.getStrategyGrid();
			EDirection leftDir = direction.getNeighbor(-1);
			ShortPoint2D leftPos = leftDir.getNextHexPoint(position);
			EDirection rightDir = direction.getNeighbor(1);
			ShortPoint2D rightPos = rightDir.getNextHexPoint(position);

			if (grid.hasNoMovableAt(leftPos.getX(), leftPos.getY())) {
				return new Path(leftPos);
			} else if (grid.hasNoMovableAt(rightPos.getX(), rightPos.getY())) {
				return new Path(rightPos);
			} else {
				EDirection twoLeftDir = direction.getNeighbor(-2);
				ShortPoint2D twoLeftPos = twoLeftDir.getNextHexPoint(position);
				EDirection twoRightDir = direction.getNeighbor(2);
				ShortPoint2D twoRightPos = twoRightDir.getNextHexPoint(position);

				if (grid.hasNoMovableAt(twoLeftPos.getX(), twoLeftPos.getY())) {
					return new Path(twoLeftPos);
				} else if (grid.hasNoMovableAt(twoRightPos.getX(), twoRightPos.getY())) {
					return new Path(twoRightPos);
				} else {
					return path;
				}
			}
		} else {
			return super.findWayAroundObstacle(direction, position, path);
		}
	}
}
