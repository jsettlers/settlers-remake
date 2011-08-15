package jsettlers.common.statistics;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;

public interface IStatisticable {
	int getNumberOf(EMaterialType materialType);

	int getNumberOf(EMovableType movableType);

	int getJoblessBearers();
}
