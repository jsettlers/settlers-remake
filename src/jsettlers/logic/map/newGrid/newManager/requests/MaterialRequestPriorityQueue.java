package jsettlers.logic.map.newGrid.newManager.requests;

import java.io.Serializable;
import java.util.Arrays;

import jsettlers.common.utils.collections.list.DoubleLinkedList;
import jsettlers.logic.map.newGrid.newManager.EMaterialPriority;

/**
 * This class is a priority queue for material requests. The possible priorities are specified in the {@link EMaterialPriority} enum.
 * 
 * @author Andreas Eberle
 * 
 */
public final class MaterialRequestPriorityQueue implements Serializable {
	private static final long serialVersionUID = 4856036773080549412L;

	private static final int DEFAULT_INSERT_PRIORITY = EMaterialPriority.LOW.ordinal;

	private final DoubleLinkedList<MaterialRequestPriorityQueueItem>[] queues;

	public MaterialRequestPriorityQueue() {
		this.queues = DoubleLinkedList.getArray(EMaterialPriority.NUMBER_OF_PRIORITIES);
	}

	/**
	 * Updates the priority of the given queue item from the oldPriority to the newPriority.
	 * <p />
	 * NOTE: The given {@link MaterialRequestPriorityQueueItem} must be in the queue with the given oldPriority! There will be no checks!
	 * 
	 * @param oldPriority
	 * @param newPriority
	 * @param queueItem
	 */
	void updatePriority(EMaterialPriority oldPriority, EMaterialPriority newPriority, MaterialRequestPriorityQueueItem queueItem) {
		queues[oldPriority.ordinal].remove(queueItem);
		queues[newPriority.ordinal].pushFront(queueItem); // TODO @Andreas Eberle: check if this should be pushEnd()
	}

	/**
	 * Inserts the request with the default priority.
	 * 
	 * @param materialRequest
	 *            The {@link MaterialRequestPriorityQueueItem} that shall be inserted.
	 */
	public void insertRequest(MaterialRequestPriorityQueueItem materialRequest) {
		queues[DEFAULT_INSERT_PRIORITY].pushEnd(materialRequest);
		materialRequest.requestQueue = this;
	}

	/**
	 * 
	 * @return Returns request with the highest priority<br>
	 *         or null if none exists.
	 */
	public MaterialRequestPriorityQueueItem getHighestRequest() {
		MaterialRequestPriorityQueueItem result = null;

		// Start with highest priority to lower ones. Skip the EPriority.STOPPED queue (index 0)
		for (int prio = EMaterialPriority.NUMBER_OF_PRIORITIES - 1; prio >= 1; prio--) {
			DoubleLinkedList<MaterialRequestPriorityQueueItem> queue = queues[prio];

			int numberOfElements = queue.size();

			for (int handledElements = 0; handledElements < numberOfElements; handledElements++) {
				result = queue.getFront();

				int inDelivery = result.inDelivery;
				int stillNeeded = result.getStillNeeded();

				// if the request is done
				if (stillNeeded <= 0) {
					result.requestQueue = null;
					queue.popFront(); // remove the request
				}

				// if all needed are in delivery, or there can not be any more in delivery
				else if (stillNeeded <= inDelivery || inDelivery >= result.getInDeliveryable()) {
					queue.pushEnd(queue.popFront()); // move the request to the end.
				}

				// everything fine, take this request
				else {
					return result;
				}
			}
		}

		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MaterialRequestPriorityQueue other = (MaterialRequestPriorityQueue) obj;
		if (!Arrays.equals(queues, other.queues))
			return false;
		return true;
	}

}
