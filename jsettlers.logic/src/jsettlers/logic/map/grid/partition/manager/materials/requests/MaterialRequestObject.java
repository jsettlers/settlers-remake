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

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ILocatable;
import jsettlers.common.utils.collections.list.DoubleLinkedListItem;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IMaterialRequest;

/**
 * This class defines a {@link DoubleLinkedListItem} that can be used by the {@link AbstractMaterialRequestPriorityQueue}.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class MaterialRequestObject extends DoubleLinkedListItem<MaterialRequestObject> implements ILocatable,
		IMaterialRequest {
	private static final long serialVersionUID = -5941459671438965185L;

	private EPriority priority = EPriority.DEFAULT;
	AbstractMaterialRequestPriorityQueue requestQueue;
	int inDelivery;

	/**
	 * Updates the priority of this {@link MaterialRequestObject} to the given {@link EPriority}.
	 * 
	 * @param newPriority
	 *            The new priority of this {@link MaterialRequestObject}.
	 */
	public final void updatePriority(EPriority newPriority) {
		if (requestQueue != null && newPriority != priority) {
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
	protected abstract short getStillNeeded();

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
	public void deliveryAborted() {
		inDelivery--;
	}

	@Override
	public boolean isActive() {
		return priority != EPriority.STOPPED && inDelivery <= getStillNeeded() && inDelivery <= getInDeliveryable();
	}

	protected abstract boolean isRoundRobinRequest();

	protected abstract EBuildingType getBuildingType();

	/**
	 * If this is a request to move the material to a stock building.
	 * 
	 * @return <code>true</code> if this is a stock stack.
	 */
	public boolean isStockRequest() {
		return false;
	}

	protected int getInDelivery() {
		return inDelivery;
	}

	public boolean isinDelivery() {
		return inDelivery > 0;
	}
}
