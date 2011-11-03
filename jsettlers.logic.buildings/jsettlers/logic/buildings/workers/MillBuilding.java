package jsettlers.logic.buildings.workers;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;

public class MillBuilding extends WorkerBuilding implements IBuilding.Mill {

	private boolean working;

	public MillBuilding(EBuildingType type, byte player) {
		super(type, player);
	}

	@Override
	public boolean isWorking() {
		return working;
	}

	public void setWorking(boolean b) {
		working = b;
	}

}
