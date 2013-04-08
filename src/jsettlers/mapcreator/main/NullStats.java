package jsettlers.mapcreator.main;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.statistics.IStatisticable;

public class NullStats implements IStatisticable {

	@Override
	public int getGameTime() {
		return 0;
	}

	@Override
	public int getNumberOf(EMaterialType materialType) {
		return 0;
	}

	@Override
	public int getNumberOf(EMovableType movableType) {
		return 0;
	}

	@Override
	public int getJoblessBearers() {
		return 0;
	}

}
