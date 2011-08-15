package jsettlers.logic.buildings;

import jsettlers.common.buildings.EBuildingType;

public class Tower extends OccupyingBuilding {

	public Tower(byte player) {
		super(EBuildingType.TOWER, player);
	}

	@Override
	public boolean isOccupied() {
		// TODO Auto-generated method stub
		return false;
	}

}
