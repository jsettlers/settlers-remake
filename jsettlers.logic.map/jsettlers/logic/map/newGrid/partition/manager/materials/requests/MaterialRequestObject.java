package jsettlers.logic.map.newGrid.partition.manager.materials.requests;

import jsettlers.common.material.EPriority;
import jsettlers.common.position.ILocatable;
import jsettlers.common.utils.collections.list.DoubleLinkedListItem;
import jsettlers.logic.map.newGrid.partition.manager.materials.interfaces.IMaterialRequest;

/**
 * This class defines a {@link DoubleLinkedListItem} that can be used by the {@link MaterialRequestPriorityQueue}.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class MaterialRequestObject extends DoubleLinkedListItem<MaterialRequestObject> implements ILocatable,
		IMaterialRequest {
	private static final long serialVersionUID = -5941459671438965185L;

	private EPriority priority = EPriority.LOW;
	MaterialRequestPriorityQueue requestQueue;
	int inDelivery;

	/**
	 * Updates the priority of this {@link MaterialRequestObject} to the given {@link EPriority}.
	 * 
	 * @param newPriority
	 *            The new priority of this {@link MaterialRequestObject}.
	 */
	public final void updatePriority(EPriority newPriority) {
		if (newPriority != priority) {
			requestQueue.updatePriority(priority, newPriority, this);
			this.priority = newPriority;
		}
	}

	/**
	 * 
	 * @return Returns true if this {@link MaterialRequestObject} is in a queue.
	 */
	protected final boolean isInQueue() {
		return requestQueue != null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MaterialRequestObject other = (MaterialRequestObject) obj;
		if (inDelivery != other.inDelivery)
			return false;
		if (priority != other.priority)
			return false;
		return true;
	}

	/**
	 * 
	 * @return Returns the number of materials that are still needed by this {@link MaterialRequestObject}.<br>
	 *         (That means, that materials that are in delivery are not counted here!)
	 */
	protected abstract int getStillNeeded();

	/**
	 * Gets the number of deliveries that can currently be done in parallel.
	 * 
	 * @return The max number of request parts that can be in delivery.
	 */
	protected abstract int getInDeliveryable();

	@Override
	public void deliveryAccepted() {
		inDelivery++;
	}

	@Override
	public final void deliveryFulfilled() {
		materialDelivered();
		inDelivery--;
	}

	/**
	 * This method is called when a materials has been delivered.
	 */
	protected abstract void materialDelivered();

	@Override
	public final void deliveryAborted() {
		inDelivery--;
	}

	@Override
	public boolean isActive() {
		return priority != EPriority.STOPPED && inDelivery <= getStillNeeded() && inDelivery <= getInDeliveryable();
	}

	protected abstract boolean isRoundRobinRequest();
}
