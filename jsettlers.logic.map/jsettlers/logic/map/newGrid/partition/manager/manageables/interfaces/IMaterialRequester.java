package jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces;

import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.newGrid.partition.manager.PartitionManager;

/**
 * This interface defines methods needed to be able to request a material from the {@link PartitionManager}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IMaterialRequester extends ILocatable {
	/**
	 * @return the position where the requested material should be delivered.
	 */
	@Override
	ISPosition2D getPos();

	/**
	 * 
	 * @return true if the request is still active,<br>
	 *         false if the request has been canceled.
	 */
	boolean isActive();

	/**
	 * This method is called when a bearer wasn't able to finish the request.
	 */
	void requestFailed();
}
