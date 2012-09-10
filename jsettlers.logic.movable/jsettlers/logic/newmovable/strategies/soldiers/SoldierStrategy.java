package jsettlers.logic.newmovable.strategies.soldiers;

import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.military.IBuildingOccupyableMovable;
import jsettlers.logic.buildings.military.IOccupyableBuilding;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.newmovable.NewMovableStrategy;
import jsettlers.logic.newmovable.interfaces.IAttackable;

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
				super.goToPos(enemy.getPos());
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
	public void leaveOccupyableBuilding() {
		super.enableNothingToDoAction(true);
		super.setVisible(true);

		state = ESoldierState.AGGRESSIVE;
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
	}

	@Override
	protected void informAboutAttackable(IAttackable other) {
		if (state == ESoldierState.AGGRESSIVE) {
			if (enemy == null) { // if no enemy is set yet
				enemy = other;
				state = ESoldierState.ENEMY_FOUND;
			} else {
				ShortPoint2D pos = super.getPos();
				if (other.getPos().getOnGridDistTo(pos) < enemy.getPos().getOnGridDistTo(pos)) { // or if the new enemy is closer
					enemy = other;
				}
			}
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
}
