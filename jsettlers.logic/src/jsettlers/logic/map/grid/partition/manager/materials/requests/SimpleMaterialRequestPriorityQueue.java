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

import java.util.Arrays;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.collections.list.DoubleLinkedList;

/**
 * This class is a simple priority queue for material requests. The possible priorities are specified in the {@link EPriority} enum.
 * 
 * @author Andreas Eberle
 * 
 */
public final class SimpleMaterialRequestPriorityQueue extends AbstractMaterialRequestPriorityQueue {
	private static final long serialVersionUID = 4856036773080549412L;

	private final DoubleLinkedList<MaterialRequestObject> queues[] = DoubleLinkedList.getArray(EPriority.NUMBER_OF_PRIORITIES);

	@Override
	protected DoubleLinkedList<MaterialRequestObject> getQueue(EPriority priority, EBuildingType buildingType, boolean stockRequest) {
		return queues[priority.ordinal];
	}

	@Override
	protected MaterialRequestObject getRequestForPrio(int prio) {
		return getRequestFrom(queues[prio]);
	}

	@Override
	public void moveObjectsOfPositionTo(ShortPoint2D position, AbstractMaterialRequestPriorityQueue newAbstractQueue) {
		assert newAbstractQueue instanceof SimpleMaterialRequestPriorityQueue : "can't move positions between diffrent types of queues.";

		SimpleMaterialRequestPriorityQueue newQueue = (SimpleMaterialRequestPriorityQueue) newAbstractQueue;

		for (int queueIdx = 0; queueIdx < queues.length; queueIdx++) {
			moveBetweenQueues(position, newQueue, queues[queueIdx], newQueue.queues[queueIdx]);
		}
	}

	@Override
	public void mergeInto(AbstractMaterialRequestPriorityQueue newAbstractQueue) {
		assert newAbstractQueue instanceof SimpleMaterialRequestPriorityQueue : "can't move positions between diffrent types of queues.";

		SimpleMaterialRequestPriorityQueue newQueue = (SimpleMaterialRequestPriorityQueue) newAbstractQueue;

		for (int prioIdx = 0; prioIdx < queues.length; prioIdx++) {
			mergeQueues(newQueue, queues[prioIdx], newQueue.queues[prioIdx]);
		}
	}

	@Override
	public boolean hasOnlyStockRequests() {
		for (DoubleLinkedList<MaterialRequestObject> q : queues) {
			if (!hasOnlyStockRequests(q)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(queues);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleMaterialRequestPriorityQueue other = (SimpleMaterialRequestPriorityQueue) obj;
		if (!Arrays.equals(queues, other.queues))
			return false;
		return true;
	}
}
