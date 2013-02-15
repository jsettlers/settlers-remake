package jsettlers.logic.map.newGrid.newManager.interfaces;

/**
 * This interface defines the methods needed by a {@link IManagerBearer} to be able to carry an offer to a request.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IMaterialRequest {
	/**
	 * Signals that this {@link IMaterialRequest} is in delivery.
	 */
	public void setInDelivery();

	/**
	 * Signals that the delivery has successfully been handled.
	 */
	public void deliveryFulfilled();

	/**
	 * Signals that the delivery has been aborted.
	 */
	public void deliveryAborted();
}
