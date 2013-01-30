package jsettlers.logic.map.newGrid.partition;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.statistics.IStatisticable;

/**
 * This class stores several statistical values needed by the UI and some logic components.
 * 
 * @author Andreas Eberle
 * 
 */
public class PartitionsStatistics implements IStatisticable {
	private final int[] numberOfMaterials = new int[EMaterialType.NUMBER_OF_MATERIALS];
	private final int[] numberOfMovables = new int[EMovableType.NUMBER_OF_MOVABLETYPES];
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
