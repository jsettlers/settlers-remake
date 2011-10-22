package jsettlers.logic.map.newGrid.partition.manager.manageables;

import jsettlers.common.map.shapes.FreeMapArea;

public interface IManageableDigger extends IManageable {

	void setDiggerJob(FreeMapArea buildingArea, byte targetHeight);

}
