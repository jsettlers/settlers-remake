package jsettlers.logic.map.newGrid.partition.manageables;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ISPosition2D;

public interface IManageableBearer extends IManageable {

	void executeJob(ISPosition2D offer, ISPosition2D request, EMaterialType materialType);

}
