package jsettlers.logic.movable.soldiers.behaviors;

import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.buildings.military.IOccupyableBuilding;

/**
 * 
 * @author Andreas Eberle
 * 
 */
class GoToTowerBehavior extends SoldierBehavior {
	private static final long serialVersionUID = 1142673726182882724L;

	private final IOccupyableBuilding building;

	GoToTowerBehavior(ISoldierBehaviorable soldier, IOccupyableBuilding building) {
		super(soldier);
		this.building = building;
	}

	@Override
	public SoldierBehavior calculate(ISPosition2D pos, IPathCalculateable pathCalcable) {
		super.getSoldier().calculatePathTo(building.getDoor());
		switch (super.getSoldier().getSoldierType()) {
		case BOWMAN:
			return new BowmanInTowerBehavior(super.getSoldier(), building);
		case INFANTARY:
		default:
			return new InfantryInTowerBehavior(super.getSoldier(), building);
		}
	}

	@Override
	public void pathRequestFailed() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean checkGoStepPrecondition() {
		return building.isNotDestroyed() && building.getPlayer() == super.getPlayer();
	}

}
