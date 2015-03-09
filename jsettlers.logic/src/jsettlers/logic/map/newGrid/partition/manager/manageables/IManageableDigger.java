package jsettlers.logic.map.newGrid.partition.manager.manageables;

import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IDiggerRequester;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface IManageableDigger extends IManageable {

	/**
	 * 
	 * @param requester
	 * @return true if the job can be handled by this digger, false if another digger needs to be asked.
	 */
	boolean setDiggerJob(IDiggerRequester requester);

}
