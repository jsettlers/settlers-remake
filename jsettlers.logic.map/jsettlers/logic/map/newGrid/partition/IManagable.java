package jsettlers.logic.map.newGrid.partition;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ISPosition2D;

public interface IManagable {

	void executeJob(ISPosition2D offer, ISPosition2D request, EMaterialType materialType);

}
