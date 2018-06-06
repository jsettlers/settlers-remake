/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.collections.list.DoubleLinkedList;
import jsettlers.logic.map.grid.partition.manager.settings.MaterialDistributionSettings;

/**
 * This class is an advanced priority queue for material requests. The requests are served according to the settings. The settings specify the probability that a given type of building will be served.
 *
 * @author Andreas Eberle
 *
 */
public final class MaterialsForBuildingsRequestPriorityQueue extends AbstractMaterialRequestPriorityQueue {
	private static final long serialVersionUID = 4856036773080549412L;

	private static final EBuildingType[] ADDITIONAL_BUILDING_TYPES = { EBuildingType.STOCK, EBuildingType.HARBOR, EBuildingType.MARKET_PLACE };
	private static final int NUMBER_OF_ADDITIONAL_BUILDING_TYPES = ADDITIONAL_BUILDING_TYPES.length;

	private final DoubleLinkedList<MaterialRequestObject> queues[][];

	private final MaterialDistributionSettings settings;
	private final EBuildingType[] buildingTypes;
	private final int numberOfAllBuildings;
	private final int numberOfConfigurableBuildings;

	private transient int[] buildingTypesToIndex;

	@SuppressWarnings("unchecked")
	public MaterialsForBuildingsRequestPriorityQueue(MaterialDistributionSettings settings) {
		this.settings = settings;

		buildingTypes = settings.getBuildingTypes();
		numberOfConfigurableBuildings = buildingTypes.length;
		numberOfAllBuildings = numberOfConfigurableBuildings + NUMBER_OF_ADDITIONAL_BUILDING_TYPES;

		queues = new DoubleLinkedList[EPriority.NUMBER_OF_PRIORITIES][];
		for (int i = 0; i < queues.length; i++) {
			queues[i] = DoubleLinkedList.getArray(numberOfAllBuildings);
		}

		calculateBuildingTypesToIndex();
	}

	private void calculateBuildingTypesToIndex() {
		buildingTypesToIndex = new int[EBuildingType.NUMBER_OF_BUILDINGS];

		for (int i = 0; i < buildingTypesToIndex.length; i++) {
			buildingTypesToIndex[i] = -1;
		}
		for (int i = 0; i < buildingTypes.length; i++) {
			buildingTypesToIndex[buildingTypes[i].ordinal] = i;
		}
		for (int i = 0; i < NUMBER_OF_ADDITIONAL_BUILDING_TYPES; i++) {
			buildingTypesToIndex[ADDITIONAL_BUILDING_TYPES[i].ordinal] = buildingTypes.length + i;
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
	protected MaterialRequestObject getRequestForPriority(int priority) {
		DoubleLinkedList<MaterialRequestObject>[] queues = this.queues[priority];

		EBuildingType randomStartBuilding = settings.drawRandomBuilding();
		if (randomStartBuilding == null) {
			return null;
		}
		int randomStartIndex = buildingTypesToIndex[randomStartBuilding.ordinal];

		for (int i = 0; i < numberOfAllBuildings; i++) {
			int buildingIdx = (i + randomStartIndex) % numberOfAllBuildings;

			// if this building type should not receive any materials, skip it; if it is additional building, always check it
			if (buildingIdx < numberOfConfigurableBuildings && settings.getDistributionProbability(buildingTypes[buildingIdx]) <= 0.0f) {
				continue;
			}

			MaterialRequestObject foundRequest = findRequestInQueue(queues[buildingIdx]);
			if (foundRequest != null) {
				return foundRequest;
			}
		}

		return null;
	}

	@Override
	public void moveObjectsOfPositionTo(ShortPoint2D position, AbstractMaterialRequestPriorityQueue newAbstractQueue) {
		assert newAbstractQueue instanceof MaterialsForBuildingsRequestPriorityQueue : "can't move positions between different types of queues.";

		MaterialsForBuildingsRequestPriorityQueue newQueue = (MaterialsForBuildingsRequestPriorityQueue) newAbstractQueue;

		for (int priorityIndex = 0; priorityIndex < queues.length; priorityIndex++) {
			DoubleLinkedList<MaterialRequestObject>[] priorityQueue = queues[priorityIndex];
			for (int queueIdx = 0; queueIdx < numberOfConfigurableBuildings; queueIdx++) {
				Iterator<MaterialRequestObject> iterator = priorityQueue[queueIdx].iterator();
				while (iterator.hasNext()) {
					MaterialRequestObject curr = iterator.next();
					if (curr.getPosition().equals(position)) {
						iterator.remove();
						newQueue.queues[priorityIndex][queueIdx].pushEnd(curr);
						curr.requestQueue = newQueue;
					}
				}
			}
		}
	}

	@Override
	public void mergeInto(AbstractMaterialRequestPriorityQueue newAbstractQueue) {
		assert newAbstractQueue instanceof MaterialsForBuildingsRequestPriorityQueue : "can't move positions between different types of queues.";

		MaterialsForBuildingsRequestPriorityQueue newQueue = (MaterialsForBuildingsRequestPriorityQueue) newAbstractQueue;

		for (int priorityIndex = 0; priorityIndex < queues.length; priorityIndex++) {
			for (int queueIdx = 0; queueIdx < numberOfConfigurableBuildings; queueIdx++) {
				DoubleLinkedList<MaterialRequestObject> currList = queues[priorityIndex][queueIdx];
				DoubleLinkedList<MaterialRequestObject> newList = newQueue.queues[priorityIndex][queueIdx];
				for (MaterialRequestObject request : currList) {
					request.requestQueue = newQueue;
				}
				currList.mergeInto(newList);
			}
		}
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MaterialsForBuildingsRequestPriorityQueue other = (MaterialsForBuildingsRequestPriorityQueue) obj;
		if (!Arrays.equals(buildingTypesToIndex, other.buildingTypesToIndex)) {
			return false;
		}
		if (!Arrays.deepEquals(queues, other.queues)) {
			return false;
		}
		if (settings == null) {
			if (other.settings != null) {
				return false;
			}
		} else if (!settings.equals(other.settings)) {
			return false;
		}
		return true;
	}
}
