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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Iterator;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.partition.IMaterialsDistributionSettings;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.collections.list.DoubleLinkedList;
import jsettlers.logic.constants.MatchConstants;

/**
 * This class is an advanced priority queue for material requests. The requests are served according to the settings. The settings specify the
 * probability that a given type of building will be served.
 * 
 * @author Andreas Eberle
 * 
 */
public final class MaterialsForBuildingsRequestPrioQueue extends AbstractMaterialRequestPriorityQueue {
	private static final long serialVersionUID = 4856036773080549412L;

	private final DoubleLinkedList<MaterialRequestObject> queues[][];

	private final IMaterialsDistributionSettings settings;

	private transient int[] buildingTypesToIndex;

	@SuppressWarnings("unchecked")
	public MaterialsForBuildingsRequestPrioQueue(IMaterialsDistributionSettings settings) {
		this.settings = settings;

		queues = new DoubleLinkedList[EPriority.NUMBER_OF_PRIORITIES][];
		for (int i = 0; i < queues.length; i++) {
			queues[i] = DoubleLinkedList.getArray(settings.getNumberOfBuildings());
		}

		calculateBuildingTypesToIndex();
	}

	private void calculateBuildingTypesToIndex() {
		buildingTypesToIndex = new int[EBuildingType.NUMBER_OF_BUILDINGS];

		for (int i = 0; i < buildingTypesToIndex.length; i++) {
			buildingTypesToIndex[i] = -1;
		}
		for (int i = 0; i < settings.getNumberOfBuildings(); i++) {
			buildingTypesToIndex[settings.getBuildingType(i).ordinal] = i;
		}
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		calculateBuildingTypesToIndex();
	}

	@Override
	protected DoubleLinkedList<MaterialRequestObject> getQueue(EPriority priority, EBuildingType buildingType) {
		int buildingIndex = buildingTypesToIndex[buildingType.ordinal];

		assert buildingIndex >= 0 : "Unknown building for this queue: " + buildingType;

		return queues[priority.ordinal][buildingIndex];
	}

	@Override
	protected MaterialRequestObject getRequestForPrio(int prio) {
		DoubleLinkedList<MaterialRequestObject>[] queues = this.queues[prio];

		int startIndex = getRandomStartIndex();

		final int numberOfBuildings = settings.getNumberOfBuildings();
		for (int i = 0; i < numberOfBuildings; i++) {
			int buildingIdx = (i + startIndex) % numberOfBuildings;

			if (settings.getProbablity(buildingIdx) <= 0.0f) // if this building type should not receive any materials, skip it
				continue;

			DoubleLinkedList<MaterialRequestObject> queue = queues[buildingIdx];

			int numberOfElements = queue.size();

			for (int handledElements = 0; handledElements < numberOfElements; handledElements++) {
				MaterialRequestObject result = queue.getFront();

				int inDelivery = result.inDelivery;
				int stillNeeded = result.getStillNeeded();

				// if the request is done
				if (stillNeeded <= 0) {
					result.requestQueue = null;
					queue.popFront(); // remove the request
					numberOfElements--;
				}

				// if all needed are in delivery, or there can not be any more in delivery
				else if (stillNeeded <= inDelivery || inDelivery >= result.getInDeliveryable()) {
					queue.pushEnd(queue.popFront()); // move the request to the end.
				}

				// everything fine, take this request
				else {
					if (result.isRoundRobinRequest()) {
						queue.pushEnd(queue.popFront()); // put the request to the end of the queue.
					}

					return result;
				}
			}
		}

		return null;
	}

	private int getRandomStartIndex() {
		float randomNumber = MatchConstants.random().nextFloat();
		float sum = 0;

		int numberOfBuildings = settings.getNumberOfBuildings();
		for (int i = 0; i < numberOfBuildings; i++) {
			sum += settings.getProbablity(i);
			if (randomNumber < sum) {
				return i;
			}
		}

		System.err.println("ERROR: No correct material distribution!");
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(buildingTypesToIndex);
		result = prime * result + Arrays.hashCode(queues);
		result = prime * result + ((settings == null) ? 0 : settings.hashCode());
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
		MaterialsForBuildingsRequestPrioQueue other = (MaterialsForBuildingsRequestPrioQueue) obj;
		if (!Arrays.equals(buildingTypesToIndex, other.buildingTypesToIndex))
			return false;
		if (!Arrays.deepEquals(queues, other.queues))
			return false;
		if (settings == null) {
			if (other.settings != null)
				return false;
		} else if (!settings.equals(other.settings))
			return false;
		return true;
	}

	@Override
	public void moveObjectsOfPositionTo(ShortPoint2D position, AbstractMaterialRequestPriorityQueue newAbstractQueue) {
		assert newAbstractQueue instanceof MaterialsForBuildingsRequestPrioQueue : "can't move positions between diffrent types of queues.";

		MaterialsForBuildingsRequestPrioQueue newQueue = (MaterialsForBuildingsRequestPrioQueue) newAbstractQueue;
		final int numberOfBuildings = settings.getNumberOfBuildings();

		for (int prioIdx = 0; prioIdx < queues.length; prioIdx++) {
			DoubleLinkedList<MaterialRequestObject>[] prioQueue = queues[prioIdx];
			for (int queueIdx = 0; queueIdx < numberOfBuildings; queueIdx++) {
				Iterator<MaterialRequestObject> iter = prioQueue[queueIdx].iterator();
				while (iter.hasNext()) {
					MaterialRequestObject curr = iter.next();
					if (curr.getPos().equals(position)) {
						iter.remove();
						newQueue.queues[prioIdx][queueIdx].pushEnd(curr);
						curr.requestQueue = newQueue;
					}
				}
			}
		}
	}

	@Override
	public void mergeInto(AbstractMaterialRequestPriorityQueue newAbstractQueue) {
		assert newAbstractQueue instanceof MaterialsForBuildingsRequestPrioQueue : "can't move positions between diffrent types of queues.";

		MaterialsForBuildingsRequestPrioQueue newQueue = (MaterialsForBuildingsRequestPrioQueue) newAbstractQueue;
		final int numberOfBuildings = settings.getNumberOfBuildings();

		for (int prioIdx = 0; prioIdx < queues.length; prioIdx++) {
			for (int queueIdx = 0; queueIdx < numberOfBuildings; queueIdx++) {
				DoubleLinkedList<MaterialRequestObject> currList = queues[prioIdx][queueIdx];
				DoubleLinkedList<MaterialRequestObject> newList = newQueue.queues[prioIdx][queueIdx];
				for (MaterialRequestObject request : currList) {
					request.requestQueue = newQueue;
				}
				currList.mergeInto(newList);
			}
		}
	}

}
