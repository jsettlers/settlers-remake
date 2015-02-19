package jsettlers.logic.map.newGrid.partition.manager.materials.requests;

import java.io.Serializable;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.collections.list.DoubleLinkedList;

/**
 * This class is an abstract priority queue for material requests. The possible priorities are specified in the {@link EPriority} enum.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class AbstractMaterialRequestPriorityQueue implements Serializable {
	private static final long serialVersionUID = 4856036773080549412L;

	/**
	 * Updates the priority of the given queue item from the oldPriority to the newPriority.
	 * <p />
	 * NOTE: The given {@link MaterialRequestObject} must be in the queue with the given oldPriority! There will be no checks!
	 * 
	 * @param oldPriority
	 * @param newPriority
	 * @param materialRequest
	 */
	final void updatePriority(EPriority oldPriority, EPriority newPriority, MaterialRequestObject materialRequest) {
		EBuildingType buildingType = materialRequest.getBuildingType();
		getQueue(oldPriority, buildingType).remove(materialRequest);
		getQueue(newPriority, buildingType).pushFront(materialRequest); // TODO @Andreas Eberle: check if this should be pushEnd()
	}

	/**
	 * Inserts the request with the default priority.
	 * 
	 * @param materialRequest
	 *            The {@link MaterialRequestObject} that shall be inserted.
	 */
	public final void insertRequest(MaterialRequestObject materialRequest) {
		getQueue(EPriority.DEFAULT, materialRequest.getBuildingType()).pushEnd(materialRequest);
		materialRequest.requestQueue = this;
	}

	/**
	 * 
	 * @return Returns request with the highest priority<br>
	 *         or null if none exists.
	 */
	public final MaterialRequestObject getHighestRequest() {
		// Start with highest priority to lower ones. Skip the EPriority.STOPPED queue (index 0)
		for (int prio = EPriority.NUMBER_OF_PRIORITIES - 1; prio >= 1; prio--) {
			MaterialRequestObject request = getRequestForPrio(prio);
			if (request != null) {
				return request;
			}
		}

		return null;
	}

	/**
	 * Gets the queue for the given priority and buildingType.
	 * 
	 * @param priority
	 *            The priority of the element.
	 * @param buildingType
	 *            The type of the building that is requesting.
	 * 
	 * @return Returns the queue for given priority and building type.
	 */
	protected abstract DoubleLinkedList<MaterialRequestObject> getQueue(EPriority priority, EBuildingType buildingType);

	/**
	 * 
	 * @param prio
	 *            Ordinal value of the priority.
	 * @return
	 */
	protected abstract MaterialRequestObject getRequestForPrio(int prio);

	@Override
	public abstract boolean equals(Object obj);

	/**
	 * Removes any requests that are at the given position from this queue and adds them to the given queue.
	 * 
	 * @param position
	 *            The position to be checked. Any request at this position will be moved to the given queue.
	 * @param newQueue
	 *            The queue that receives the objects removed from this queue.
	 */
	public abstract void moveObjectsOfPositionTo(ShortPoint2D position, AbstractMaterialRequestPriorityQueue newQueue);

	/**
	 * Merges this queue into the given {@link AbstractMaterialRequestPriorityQueue}.
	 * <p />
	 * NOTE: The given {@link AbstractMaterialRequestPriorityQueue} must be of the exact same sub type of {@link AbstractMaterialRequestPriorityQueue}
	 * as this queue is! (i.e. this.getClass().equals(newQueue.getCLass()) must be true!)
	 * 
	 * @param newQueue
	 *            The new queue where the data of this queue will be merged into.
	 */
	public abstract void mergeInto(AbstractMaterialRequestPriorityQueue newQueue);

}
