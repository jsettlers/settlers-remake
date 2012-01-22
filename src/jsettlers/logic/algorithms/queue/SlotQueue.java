package jsettlers.logic.algorithms.queue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ISPosition2D;

/**
 * This is a queue that lets you define different slots. In each slot, the entries are processed in a fifo-principle, but it is not guaranteed that
 * the first job is handled first, but it gets higher priority.
 * <p>
 * This queue is not thread safe.
 * 
 * @param T
 *            the type of the slot identifiers. Should support equals
 * @param E
 *            the type of elements in the queue.
 * @author michael
 */
public class SlotQueue<T, E extends ILocatable> implements Serializable {
	private static final long serialVersionUID = -5567867841476892036L;

	private static final int ELEMENTS_TO_LOOK_AT = 15;
	private static final int CONSIDER_MAX = 3; // < should be small

	private T[] slotTypes;
	private int[] slotPriority;
	/**
	 * The head of the fifo queues
	 */
	private ElementHolder<E>[] slots;
	/**
	 * The tail of the fifo queues
	 */
	private ElementHolder<E>[] tails;
	private int[] count;
	/**
	 * The permutation of the slots so that they are ordered by priority.
	 */
	private int[] slotOrder;

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.writeObject(slotTypes);
		oos.writeObject(slotPriority);
		oos.writeObject(count);
		oos.writeObject(slotOrder);

		for (int i = 0; i < slotTypes.length; i++) {
			ElementHolder<E> curr = slots[i];
			while (curr != null) {
				oos.writeObject(curr);
				curr = curr.next;
			}
			oos.writeObject(null);
		}

		for (int i = 0; i < slotTypes.length; i++) {
			ElementHolder<E> curr = tails[i];
			while (curr != null) {
				oos.writeObject(curr);
				curr = curr.next;
			}
			oos.writeObject(null);
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		slotTypes = (T[]) ois.readObject();
		slotPriority = (int[]) ois.readObject();
		count = (int[]) ois.readObject();
		slotOrder = (int[]) ois.readObject();

		slots = new ElementHolder[slotTypes.length];
		for (int i = 0; i < slotTypes.length; i++) {
			ElementHolder<E> curr = (ElementHolder<E>) ois.readObject();
			slots[i] = curr;
			ElementHolder<E> last = curr;
			while (last != null) {
				curr = (ElementHolder<E>) ois.readObject();
				last.next = curr;
				last = curr;
			}
		}

		tails = new ElementHolder[slotTypes.length];
		for (int i = 0; i < slotTypes.length; i++) {
			ElementHolder<E> curr = (ElementHolder<E>) ois.readObject();
			tails[i] = curr;
			ElementHolder<E> last = curr;
			while (last != null) {
				curr = (ElementHolder<E>) ois.readObject();
				last.next = curr;
				last = curr;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public SlotQueue(T[] slottypes, int[] slotpriority) {
		int slotCount = slotpriority.length;
		if (slotCount != slottypes.length) {
			throw new IllegalArgumentException();
		}
		this.slotTypes = slottypes;
		this.slotPriority = slotpriority;

		this.slots = new ElementHolder[slotCount];
		this.tails = new ElementHolder[slotCount];
		this.count = new int[slotCount];
		this.slotOrder = new int[slotCount];
		for (int i = 0; i < slotCount; i++) {
			slotOrder[i] = i;
		}
		reorderSlots();
	}

	private void reorderSlots() {
		int slotCount = slotOrder.length;
		for (int i = 0; i < slotCount; i++) {
			for (int j = i; j < slotCount; j++) {
				if (slotPriority[slotOrder[i]] < slotPriority[slotOrder[j]]) {
					int temp = slotOrder[i];
					slotOrder[i] = slotOrder[j];
					slotOrder[j] = temp;
				}
			}
		}
	}

	/**
	 * Adds the element to the queue, if the given slot exists (otherwise: does nothing)
	 * 
	 * @param slottype
	 *            The slot to use
	 * @param element
	 *            The element to add
	 */
	public void add(T slot, E element) {
		ElementHolder<E> elementHolder = new ElementHolder<E>(element);

		for (int i = 0; i < slots.length; i++) {
			if (slotTypes[i].equals(slot)) {
				if (tails[i] == null) {
					slots[i] = elementHolder;
					tails[i] = elementHolder;
				} else {
					tails[i].next = elementHolder;
					tails[i] = elementHolder;
				}
				count[i]++;
				break;
			}
		}
	}

	/**
	 * Pops a element from a specific slot.
	 * 
	 * @param slot
	 *            The slot to use
	 * @param pos
	 *            The position to pop close to.
	 */
	public E pop(T slot, ISPosition2D pos) {
		for (int slotNumber = 0; slotNumber < slots.length; slotNumber++) {
			if (slotTypes[slotNumber].equals(slot)) {
				return pop(pos, slotNumber);
			}
		}
		return null;
	}

	private E pop(ISPosition2D pos, int slotNumber) {
		if (slots[slotNumber] == null) {
			return null;
		} else {
			ElementHolder<E> best = findBestFit(pos, slotNumber);

			// remove best from the array.
			removeFromSlot(slotNumber, best);

			E e = best.element;
			return e;
		}
	}

	private ElementHolder<E> findBestFit(ISPosition2D pos, int slotNumber) {
		ElementHolder<E> best = slots[slotNumber];
		float bestDist = Float.POSITIVE_INFINITY;
		ElementHolder<E> current = slots[slotNumber];
		for (int cost = 0; cost < ELEMENTS_TO_LOOK_AT && current != null; cost++) {
			float dist = MapCircle.getDistanceSquared(current.element.getPos(), pos);
			if (dist < bestDist) {
				best = current;
				bestDist = dist;
			}
			cost += current.skipcost;
			current = current.next;
		}
		return best;
	}

	/**
	 * Removes a element from a slot, rates the skip cost of all elements to skip up. Decreases the count for the slot by one.
	 * 
	 * @param slotNumber
	 * @param toRemove
	 */
	private void removeFromSlot(int slotNumber, ElementHolder<E> toRemove) {
		ElementHolder<E> current;
		if (toRemove == slots[slotNumber]) {
			slots[slotNumber] = toRemove.next;
			if (slots[slotNumber] == null) {
				tails[slotNumber] = null;
			}
		} else {
			for (current = slots[slotNumber]; true /* uses break */; current = current.next) {
				if (current.next == toRemove) {
					current.next = toRemove.next;
					if (toRemove.next == null) {
						tails[slotNumber] = current;
					}
					break;
				} else {
					current.skipcost++;
				}
			}
		}
		count[slotNumber]--;
	}

	/**
	 * Pops a element. The position is taken into account when rating the possible pop slots.
	 * 
	 * @param closeTo
	 * @return
	 */
	public E pop(ISPosition2D closeTo) {
		float bestDistance = Float.POSITIVE_INFINITY;
		ElementHolder<E> best = null;
		int bestSlot = 0;

		for (int i = 0, considered = 0; i < slotOrder.length && considered < CONSIDER_MAX; i++) {
			int slotNumber = slotOrder[i];

			ElementHolder<E> myBest = findBestFit(closeTo, slotNumber);
			if (myBest != null) {
				considered++;
				float distance = MapCircle.getDistanceSquared(myBest.element.getPos(), closeTo);
				if (distance < bestDistance) {
					bestDistance = distance;
					best = myBest;
					bestSlot = slotNumber;
				}
			}
		}

		if (best != null) {
			removeFromSlot(bestSlot, best);
			return best.element;
		} else {
			return null;
		}
	}

	/**
	 * Pops anything
	 * 
	 * @return
	 */
	public E pop() {
		for (int i = 0; i < slotOrder.length; i++) {
			int slotNumber = slotOrder[i];
			if (slots[slotNumber] != null) {
				E e = slots[slotNumber].element;
				slots[slotNumber] = slots[slotNumber].next;
				if (slots[slotNumber] == null) {
					tails[slotNumber] = null;
				}
				return e;
			}
		}
		return null;
	}

	/**
	 * Pushes all items at the given position to the other queue.
	 * 
	 * @param pos
	 * @param to
	 * @see #add(Object, ILocatable)
	 */
	public void moveItemsForPosition(ISPosition2D pos, SlotQueue<T, E> to) {
		for (int i = 0; i < slots.length; i++) {
			if (slots[i] == null) {
				continue;
			}

			ElementHolder<E> current;
			for (current = slots[i]; current.next != null;) {
				if (current.next.element.getPos().equals(pos)) {
					to.add(slotTypes[i], current.next.element);
					current.next = current.next.next;
					if (current.next == null) {
						tails[i] = current;
					}
				} else {
					current = current.next;
				}
			}

			if (slots[i].element.getPos().equals(pos)) {
				to.add(slotTypes[i], slots[i].element);
				slots[i] = slots[i].next;
				if (slots[i] == null) {
					tails[i] = null;
				}
			}
		}
	}

	private static class ElementHolder<E> implements Serializable {
		private static final long serialVersionUID = 5419958157372923605L;

		final E element;
		ElementHolder<E> next = null;
		int skipcost = 1;

		public ElementHolder(E element) {
			this.element = element;
		}
	}

	public void addAll(SlotQueue<T, E> other) {
		for (int i = 0; i < other.slots.length; i++) {
			for (ElementHolder<E> current = other.slots[i]; current != null; current = current.next) {
				add(other.slotTypes[i], current.element);
			}
		}
	}

	public void removeOfType(T type, ISPosition2D pos) {
		for (int slotNumber = 0; slotNumber < slots.length; slotNumber++) {
			if (slotTypes[slotNumber].equals(type) && slots[slotNumber] != null) {
				removeFromPosition(pos, slotNumber);
			}
		}
	}

	private void removeFromPosition(ISPosition2D pos, int slotNumber) {
		for (ElementHolder<E> current = slots[slotNumber]; current.next != null;) {
			if (current.next.element.getPos().equals(pos)) {
				current.next = current.next.next;
				if (current.next == null) {
					tails[slotNumber] = current;
				}
			} else {
				current = current.next;
			}
		}

		if (slots[slotNumber].element.getPos().equals(pos)) {
			slots[slotNumber] = slots[slotNumber].next;
			if (slots[slotNumber] == null) {
				tails[slotNumber] = null;
			}
		}
	}

}
