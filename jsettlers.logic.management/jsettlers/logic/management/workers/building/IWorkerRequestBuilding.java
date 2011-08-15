package jsettlers.logic.management.workers.building;

import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.management.workers.IWorkerBuilding;

/**
 * interface for a building that want's to request a worker
 * 
 * @author Andreas Eberle
 * 
 */
public interface IWorkerRequestBuilding extends IWorkerBuilding, IPlayerable {

	/**
	 * 
	 * @return gives the door position of the building. (Needed to determine the nearest settler)
	 */
	ISPosition2D getDoor();

	ISPosition2D getWorkAreaCenter();
}
