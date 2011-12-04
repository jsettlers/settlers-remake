package jsettlers.logic.map.newGrid.partition.manager.manageables;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.buildings.military.Barrack;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IMaterialRequester;

/**
 * This interface defines methods needed by a bearer to be managed by a PartitionManager.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IManageableBearer extends IManageable, ILocatable {

	void executeJob(ISPosition2D offer, IMaterialRequester requester, EMaterialType materialType);

	void becomeWorker(EMovableType movableType);

	void becomeWorker(EMovableType movableType, ISPosition2D offer);

	void becomeSoldier(Barrack barrack);

}
