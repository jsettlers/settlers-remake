package jsettlers.logic.map.newGrid.partition.manager.datastructures;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * A simple slot queue that supports {@link #pushLast(int, ElementType)} and {@link #popFront(int)} on the single slots.
 * 
 * @author Andreas Eberle
 * 
 * @param <SlotType>
 *            Type of the slot identifiers.
 * @param <ElementType>
 *            Type of the elements.
 */
public final class SimpleSlotQueue<SlotType, ElementType> implements Serializable {
	private static final long serialVersionUID = 979224996513554546L;

	private final SlotType[] slotTypes;
	private final LinkedList<ElementType>[] slotLists;

	@SuppressWarnings("unchecked")
	// this is checked.
	public SimpleSlotQueue(SlotType[] slotTypes) {
		this.slotTypes = slotTypes.clone();
		this.slotLists = (LinkedList<ElementType>[]) new LinkedList<?>[slotTypes.length];

		for (int i = 0; i < slotTypes.length; i++) {
			slotLists[i] = new LinkedList<ElementType>();
		}
	}

	public void pushLast(SlotType type, ElementType element) {
		pushLast(getSlotNumber(type), element);
	}

	public void pushLast(int slotNumber, ElementType element) {
		if (0 <= slotNumber && slotNumber < getSlotTypes().length) {
			slotLists[slotNumber].addLast(element);
		}
	}

	public ElementType popFront(SlotType type) {
		return popFront(getSlotNumber(type));
	}

	public ElementType popFront(int slotNumber) {
		if (0 <= slotNumber && slotNumber < getSlotTypes().length) {
			return slotLists[slotNumber].isEmpty() ? null : slotLists[slotNumber].pop();
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

	private int getSlotNumber(SlotType type) {
		for (int i = 0; i < getSlotTypes().length; i++) {
			if (getSlotTypes()[i].equals(type)) {
				return i;
			}
		}
		return -1;
	}

	public SlotType[] getSlotTypes() {
		return slotTypes.clone();
	}

	/**
	 * Adds all elements of the other queue to this queue. The slotTypes array of both queues must be equal.
	 * 
	 * @param other
	 *            another queue that will be merged into this queue.
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
}
