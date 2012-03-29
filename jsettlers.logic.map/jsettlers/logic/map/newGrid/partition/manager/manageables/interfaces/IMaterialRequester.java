package jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces;

import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.PartitionManager;

/**
 * This interface defines methods needed to be able to request a material from the {@link PartitionManager}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IMaterialRequester extends ILocatable, IRequester {
	/**
	 * @return the position where the requested material should be delivered.
	 */
	@Override
	ShortPoint2D getPos();

	@Override
	boolean isRequestActive();

	/**
	 * This method is called when a bearer wasn't able to finish the request.
	 */
	void requestFailed();
}
