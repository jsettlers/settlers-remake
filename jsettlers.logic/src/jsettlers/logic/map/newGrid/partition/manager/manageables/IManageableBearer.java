package jsettlers.logic.map.newGrid.partition.manager.manageables;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IBarrack;
import jsettlers.logic.map.newGrid.partition.manager.materials.interfaces.IManagerBearer;

/**
 * This interface defines methods needed by a bearer to be managed by a PartitionManager.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IManageableBearer extends IManageable, ILocatable, IManagerBearer {

	void becomeWorker(IWorkerRequester requester, EMovableType movableType);

	void becomeWorker(IWorkerRequester requester, EMovableType movableType, ShortPoint2D offer);

	void becomeSoldier(IBarrack barrack);

	/**
	 * This interface is used by the bearers to signal the need of a reoffer of the worker creation request.
	 * 
	 * @author Andreas Eberle
	 * 
	 */
	public static interface IWorkerRequester {
		void workerCreationRequestFailed(EMovableType type, ShortPoint2D position);
	}
}
