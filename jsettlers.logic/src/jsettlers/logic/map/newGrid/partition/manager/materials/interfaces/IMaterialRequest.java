package jsettlers.logic.map.newGrid.partition.manager.materials.interfaces;

import jsettlers.common.position.ILocatable;

/**
 * This interface defines the methods needed by a {@link IManagerBearer} to be able to carry an offer to a request.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IMaterialRequest extends ILocatable {
	/**
	 * Signals that this {@link IMaterialRequest} is in delivery.
	 */
	public void deliveryAccepted();

	/**
	 * Signals that the delivery has successfully been handled.
	 */
	public void deliveryFulfilled();

	/**
	 * Signals that the delivery has been aborted.
	 */
	public void deliveryAborted();

	/**
	 * Checks if the request is still active.
	 * 
	 * @return Returns true if the request is still active,<br>
	 *         otherwise it returns false.
	 */
	public boolean isActive();
}
