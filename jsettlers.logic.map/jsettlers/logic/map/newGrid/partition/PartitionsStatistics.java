package jsettlers.logic.map.newGrid.partition;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.statistics.IStatisticable;

public class PartitionsStatistics implements IStatisticable {
	private final int[] numberOfMaterials = new int[EMaterialType.values().length];
	private final int[] numberOfMovables = new int[EMovableType.values().length];
	private int joblessBearer = 0;

	@Override
	public int getNumberOf(EMaterialType materialType) {
		return numberOfMaterials[materialType.ordinal()];
	}

	@Override
	public int getNumberOf(EMovableType movableType) {
		return numberOfMovables[movableType.ordinal()];
	}

	@Override
	public int getJoblessBearers() {
		return joblessBearer;
	}

	public void reduceOwned(EMaterialType materialType) {
		numberOfMaterials[materialType.ordinal()]--;
	}

	public void increaseOwned(EMaterialType materialType) {
		numberOfMaterials[materialType.ordinal()]++;
	}

	public void reduceOwned(EMovableType movableType) {
		numberOfMovables[movableType.ordinal()]--;
	}

	public void increaseOwned(EMovableType movableType) {
		numberOfMovables[movableType.ordinal()]++;
	}

}
