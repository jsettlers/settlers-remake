package jsettlers.logic.newmovable.strategies.soldiers;

import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.military.IBuildingOccupyableMovable;
import jsettlers.logic.buildings.military.IOccupyableBuilding;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.newmovable.NewMovableStrategy;

public final class SoldierStrategy extends NewMovableStrategy implements IBuildingOccupyableMovable {
	private static final long serialVersionUID = 5246120883607071865L;
	private final EMovableType movableType;

	private ESoldierState state = ESoldierState.JOBLESS;
	private IOccupyableBuilding building;

	public SoldierStrategy(NewMovable movable, EMovableType movableType) {
		super(movable);
		this.movableType = movableType;
	}

	@Override
	protected void action() {
		switch (state) {
		case JOBLESS:
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

	@Override
	public void setOccupyableBuilding(IOccupyableBuilding building) {
		this.building = building;
		this.state = ESoldierState.INIT_GOTO_TOWER;
	}

	private static enum ESoldierState {
		JOBLESS,
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
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);
	}

}
