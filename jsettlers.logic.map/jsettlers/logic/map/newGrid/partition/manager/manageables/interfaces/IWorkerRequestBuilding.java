package jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ISPosition2D;

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

	boolean popMaterial(ISPosition2D position, EMaterialType material);
}
