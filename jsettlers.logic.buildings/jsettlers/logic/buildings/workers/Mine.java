package jsettlers.logic.buildings.workers;

import jsettlers.common.buildings.EBuildingType;

public class Mine extends WorkerBuilding {
	private static final long serialVersionUID = 9201058266194063092L;

	public Mine(EBuildingType type, byte player) {
		super(type, player);
	}

	@Override
	protected boolean shouldBeFlatened() {
		return false;
	}
}
