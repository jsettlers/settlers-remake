package jsettlers.logic.map.newGrid.newManager;

import jsettlers.common.position.ILocatable;
import jsettlers.common.utils.collections.list.DoubleLinkedListItem;

public abstract class MaterialRequestPriorityQueueItem extends DoubleLinkedListItem<MaterialRequestPriorityQueueItem> implements ILocatable {
	private static final long serialVersionUID = -5941459671438965185L;

	private EPriority priority = EPriority.LOW;
	MaterialRequestPriorityQueue requestQueue;
	int inDelivery;

	public void updatePriority(EPriority newPriority) {
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
}
