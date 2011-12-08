package jsettlers.logic.map.newGrid.partition.manager.manageables;

import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IDiggerRequester;

public interface IManageableDigger extends IManageable {

	void setDiggerJob(IDiggerRequester requester);

}
