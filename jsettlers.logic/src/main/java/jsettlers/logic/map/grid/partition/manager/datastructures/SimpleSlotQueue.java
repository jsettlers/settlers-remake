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
package jsettlers.logic.map.grid.partition.manager.datastructures;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;

/**
 * A simple slot queue that supports {@link #pushLast(int, ElementType)} and {@link #popFront(int)} on the single slots.
 *
 * @param <SlotType>
 * 		Type of the slot identifiers.
 * @param <ElementType>
 * 		Type of the elements.
 * @author Andreas Eberle
 */
public final class SimpleSlotQueue<SlotType, ElementType extends ILocatable> implements Serializable {
	private static final long serialVersionUID = 979224996513554546L;

	private final SlotType[] slotTypes;
	private final LinkedList<ElementType>[] slotLists;

	@SuppressWarnings("unchecked")
	// this is checked.
	public SimpleSlotQueue(SlotType[] slotTypes) {
		this.slotTypes = slotTypes.clone();
		this.slotLists = (LinkedList<ElementType>[]) new LinkedList<?>[slotTypes.length];

		for (int i = 0; i < slotTypes.length; i++) {
			slotLists[i] = new LinkedList<>();
		}
	}

	public void pushLast(SlotType type, ElementType element) {
		pushLast(getSlotNumber(type), element);
	}

	public void pushLast(int slotNumber, ElementType element) {
		if (0 <= slotNumber && slotNumber < slotTypes.length) {
			slotLists[slotNumber].push(element);
		}
	}

	public ElementType popFront(SlotType type) {
		return popFront(getSlotNumber(type));
	}

	public ElementType popFront(int slotNumber) {
		if (0 <= slotNumber && slotNumber < slotTypes.length) {
			return slotLists[slotNumber].pollLast();
		} else {
			return null;
		}
	}

	public boolean isSlotEmpty(SlotType type) {
		return isSlotEmpty(getSlotNumber(type));
	}

	public boolean isSlotEmpty(int slotNumber) {
		return slotLists[slotNumber].isEmpty();
	}

	private final int getSlotNumber(SlotType type) {
		final int slots = slotTypes.length;

		for (int i = 0; i < slots; i++) {
			if (slotTypes[i].equals(type)) {
				return i;
			}
		}
		return -1;
	}

	public SlotType[] getSlotTypes() {
		return slotTypes;
	}

	/**
	 * Adds all elements of the other queue to this queue. The slotTypes array of both queues must be equal.
	 *
	 * @param other
	 * 		another queue that will be merged into this queue.
	 */
	public void merge(SimpleSlotQueue<SlotType, ElementType> other) {
		if (Arrays.equals(slotTypes, other.slotTypes)) {
			for (int i = 0; i < slotTypes.length; i++) {
				slotLists[i].addAll(other.slotLists[i]);
			}
		} else {
			throw new UnsupportedOperationException("sloptTypes array of both SimpleSlotQueues must be equal!");
		}
	}

	public int getSlotSize(SlotType slotType) {
		return slotLists[getSlotNumber(slotType)].size();
	}

	public void moveItemsForPosition(ShortPoint2D position, SimpleSlotQueue<SlotType, ElementType> otherSlotQueue) {
		assert Arrays.equals(slotTypes, otherSlotQueue.slotTypes);

		for (int idx = 0; idx < slotLists.length; idx++) {
			Iterator<ElementType> iter = slotLists[idx].iterator();
			while (iter.hasNext()) {
				ElementType element = iter.next();
				if (position.equals(element.getPosition())) {
					iter.remove();
					otherSlotQueue.pushLast(idx, element);
				}
			}
		}
	}

	/**
	 * Adds all elements in the given slotQueue to this one. <br>
	 * NOTE: The queues must have an equal {@link #slotTypes} array!
	 *
	 * @param otherSlotQueue
	 * 		The other queue.
	 */
	public void addAll(SimpleSlotQueue<SlotType, ElementType> otherSlotQueue) {
		assert Arrays.equals(slotTypes, otherSlotQueue.slotTypes);

		for (int idx = 0; idx < slotLists.length; idx++) {
			slotLists[idx].addAll(otherSlotQueue.slotLists[idx]);
		}
	}
}
