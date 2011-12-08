package jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces;

import jsettlers.common.map.shapes.FreeMapArea;

public interface IDiggerRequester extends IRequester {

	FreeMapArea getBuildingArea();

	@Override
	boolean isRequestActive();

	byte getHeight();
}
