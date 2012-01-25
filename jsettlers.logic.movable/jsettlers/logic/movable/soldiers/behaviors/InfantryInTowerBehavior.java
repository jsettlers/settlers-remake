package jsettlers.logic.movable.soldiers.behaviors;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.buildings.military.IOccupyableBuilding;

/**
 * 
 * @author Andreas Eberle
 * 
 */
class InfantryInTowerBehavior extends SoldierBehavior {
	private static final long serialVersionUID = -4591371459281123071L;

	private final IOccupyableBuilding building;
	private boolean firstCall = true;

	InfantryInTowerBehavior(ISoldierBehaviorable soldier, IOccupyableBuilding building) {
		super(soldier);
		this.building = building;
	}

	@Override
	public SoldierBehavior calculate(ISPosition2D pos, IPathCalculateable pathCalcable) {
		if (firstCall) {
			firstCall = false;
			super.getSoldier().setDontMove(true);
			super.setVisible(false);
			building.setSoldier(super.getBuildingOccupier());
		}
		return this;
	}

	@Override
	public void pathRequestFailed() {
	}

	@Override
	public void killedEvent(EMovableType type) {
		building.requestFailed(type);
	}

}
