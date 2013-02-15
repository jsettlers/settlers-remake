package jsettlers.logic.map.newGrid.newManager.requests;

import jsettlers.common.position.ILocatable;
import jsettlers.common.utils.collections.list.DoubleLinkedListItem;
import jsettlers.logic.map.newGrid.newManager.EMaterialPriority;
import jsettlers.logic.map.newGrid.newManager.interfaces.IMaterialRequest;

/**
 * This class defines a {@link DoubleLinkedListItem} that can be used by the {@link MaterialRequestPriorityQueue}.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class MaterialRequestPriorityQueueItem extends DoubleLinkedListItem<MaterialRequestPriorityQueueItem> implements ILocatable,
		IMaterialRequest {
	private static final long serialVersionUID = -5941459671438965185L;

	private EMaterialPriority priority = EMaterialPriority.LOW;
	MaterialRequestPriorityQueue requestQueue;
	int inDelivery;

	public void updatePriority(EMaterialPriority newPriority) {
		if (newPriority != priority) {
			requestQueue.updatePriority(priority, newPriority, this);
			this.priority = newPriority;
		}
	}

	public boolean isInQueue() {
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
		MaterialRequestPriorityQueueItem other = (MaterialRequestPriorityQueueItem) obj;
		if (inDelivery != other.inDelivery)
			return false;
		if (priority != other.priority)
			return false;
		return true;
	}

	public abstract int getStillNeeded();

	/**
	 * Gets the number of deliveries that can be done in parallel.
	 * 
	 * @return The max number of request parts that can be in delivery.
	 */
	public abstract int getInDeliveryable();

	@Override
	public void setInDelivery() {
		inDelivery++;
	}

	@Override
	public void deliveryFulfilled() {
		materialDelivered();
		inDelivery--;
	}

	protected abstract void materialDelivered();

	@Override
	public void deliveryAborted() {
		inDelivery--;
	}
}
