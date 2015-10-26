/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.map.grid.partition.manager.materials.requests;

import java.io.Serializable;
import java.util.Iterator;

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
		getQueue(oldPriority, buildingType, materialRequest.isStockRequest()).remove(materialRequest);
		getQueue(newPriority, buildingType, materialRequest.isStockRequest()).pushFront(materialRequest); // TODO @Andreas Eberle: check if this
																											// should be pushEnd()
	}

	/**
	 * Inserts the request with the default priority.
	 * 
	 * @param materialRequest
	 *            The {@link MaterialRequestObject} that shall be inserted.
	 */
	public final void insertRequest(MaterialRequestObject materialRequest) {
		assert materialRequest.requestQueue == null;
		getQueue(EPriority.DEFAULT, materialRequest.getBuildingType(), materialRequest.isStockRequest()).pushEnd(materialRequest);
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
				assert !request.isStockRequest() || hasOnlyStockRequests();
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
	 * @param stockRequest
	 *            <code>true</code> if this is a stock building request.
	 * 
	 * @return Returns the queue for given priority and building type.
	 */
	protected abstract DoubleLinkedList<MaterialRequestObject> getQueue(EPriority priority, EBuildingType buildingType, boolean stockRequest);

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

	protected static void moveBetweenQueues(ShortPoint2D position, AbstractMaterialRequestPriorityQueue newQueue,
			DoubleLinkedList<MaterialRequestObject> queue,
			DoubleLinkedList<MaterialRequestObject> pushTo) {
		Iterator<MaterialRequestObject> iter = queue.iterator();
		while (iter.hasNext()) {
			MaterialRequestObject curr = iter.next();
			if (curr.getPos().equals(position)) {
				iter.remove();
				pushTo.pushEnd(curr);
				curr.requestQueue = newQueue;
			}
		}
	}

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

	protected static void mergeQueues(AbstractMaterialRequestPriorityQueue newQueue, DoubleLinkedList<MaterialRequestObject> currList,
			DoubleLinkedList<MaterialRequestObject> newList) {
		for (MaterialRequestObject request : currList) {
			request.requestQueue = newQueue;
		}
		currList.mergeInto(newList);
	}

	/**
	 * A helper method that takes one request form a queue.
	 * 
	 * @param queue
	 *            The queue.
	 * @return The request or <code>null</code> if none was found.
	 */
	protected static MaterialRequestObject getRequestFrom(DoubleLinkedList<MaterialRequestObject> queue) {
		int numberOfElements = queue.size();

		for (int handledElements = 0; handledElements < numberOfElements; handledElements++) {
			MaterialRequestObject request = queue.getFront();

			int inDelivery = request.inDelivery;
			int stillNeeded = request.getStillNeeded();

			// if the request is done
			if (stillNeeded <= 0) {
				request.requestQueue = null;
				queue.popFront(); // remove the request
				numberOfElements--;
			}

			// if all needed are in delivery, or there can not be any more in delivery
			else if (stillNeeded <= inDelivery || inDelivery >= request.getInDeliveryable()) {
				queue.pushEnd(queue.popFront()); // move the request to the end.
			}

			// everything fine, take this request
			else {
				if (request.isRoundRobinRequest()) {
					queue.pushEnd(queue.popFront()); // put the request to the end of the queue.
				}

				return request;
			}
		}
		return null;
	}

	public abstract boolean hasOnlyStockRequests();

	protected static boolean hasOnlyStockRequests(DoubleLinkedList<MaterialRequestObject> queue) {
		// TODO: Optimize.
		for (MaterialRequestObject request : queue) {
			int inDelivery = request.inDelivery;
			int stillNeeded = request.getStillNeeded();
			if (!request.isStockRequest() && !(stillNeeded >= 0) && !(stillNeeded <= inDelivery || inDelivery >= request.getInDeliveryable())) {
				return false;
			}
		}
		return true;
	}
}
