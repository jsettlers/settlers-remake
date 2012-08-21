package jsettlers.logic.newmovable.strategies.soldiers;

import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.military.IBuildingOccupyableMovable;
import jsettlers.logic.buildings.military.IOccupyableBuilding;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.newmovable.NewMovableStrategy;

public final class SoldierStrategy extends NewMovableStrategy implements IBuildingOccupyableMovable {
	private static final long serialVersionUID = 5246120883607071865L;
	private static final float SOLDIER_ATTACK_DURATION = 1;
	private final EMovableType movableType;

	private ESoldierState state = ESoldierState.AGGRESSIVE;
	private IOccupyableBuilding building;
	private NewMovable enemy;

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
			enemy.hit(0.1f); // decrease the enemy's health
		case ENEMY_FOUND:
			enemy = super.getStrategyGrid().getEnemyInSearchArea(super.getMovable());
			if (enemy == null) { // no enemy found, go back in normal mode
				state = ESoldierState.AGGRESSIVE;
				break;
			}

			if (isEnemyAttackable()) { // if enemy is close enough, attack it
				super.lookInDirection(EDirection.getApproxDirection(super.getPos(), enemy.getPos()));
				super.playAction(EAction.ACTION1, SOLDIER_ATTACK_DURATION);
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
			super.enableNothingToDoAction(false);
			super.setVisible(false);
			building.setSoldier(this);
			state = ESoldierState.IN_TOWER;
			break;

		case IN_TOWER:
			break;
		}
	}

	private boolean isEnemyAttackable() {
		return super.getPos().getDistTo(enemy.getPos()) == 1;
	}

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
	public ESoldierType getSoldierType() {
		switch (movableType) {
		case SWORDSMAN_L1:
		case SWORDSMAN_L2:
		case SWORDSMAN_L3:
		case PIKEMAN_L1:
		case PIKEMAN_L2:
		case PIKEMAN_L3:
			return ESoldierType.INFANTARY;

		case BOWMAN_L1:
		case BOWMAN_L2:
		case BOWMAN_L3:
			return ESoldierType.BOWMAN;

		default:
			assert false : "Soldier type unknown: " + movableType;
			return null;
		}
	}

	@Override
	public IMovable getMovable() {
		return super.getMovable();
	}

	@Override
	public void leaveOccupyableBuilding(ShortPoint2D pos) {
		super.enableNothingToDoAction(true);
		super.setPosition(pos);
		super.setVisible(true);

		state = ESoldierState.AGGRESSIVE;
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
	}

	@Override
	protected void informAboutAttackable(NewMovable other) {
		if (state == ESoldierState.AGGRESSIVE) {
			if (enemy == null) { // if no enemy is set yet
				enemy = other;
				state = ESoldierState.ENEMY_FOUND;
			} else {
				ShortPoint2D pos = super.getPos();
				if (other.getPos().getDistTo(pos) < enemy.getPos().getDistTo(pos)) { // or if the new enemy is closer
					enemy = other;
				}
			}
		}
	}

	@Override
	protected boolean checkPathStepPreconditions(ShortPoint2D pathTarget, int step) {
		return !((state == ESoldierState.ENEMY_FOUND || state == ESoldierState.HITTING) && step >= 2);
	}
}
