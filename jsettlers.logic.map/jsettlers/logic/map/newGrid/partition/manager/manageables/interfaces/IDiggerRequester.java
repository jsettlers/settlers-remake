package jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ILocatable;

public interface IDiggerRequester extends IRequester, ILocatable {
	EBuildingType getBuildingType();

	@Override
	boolean isRequestActive();

	byte getHeight();
}
