/*******************************************************************************
 * Copyright (c) 2016 - 2017
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.map.grid.partition.manager.materials.offers.list;

import java.io.Serializable;

import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.MathUtils;

import java8.util.function.Consumer;

/**
 * Created by Andreas Eberle on 23.08.2016.
 */
public class PrioritizedPositionableList<P extends Enum, T extends ILocatable & IPrioritizable<P> & IListManageable> implements Serializable {
	private final ManagingPositionableList<T>[] lists;

	@SuppressWarnings("unchecked")
	public PrioritizedPositionableList(int numberOfPriorities) {
		lists = new ManagingPositionableList[numberOfPriorities];
		for (int i = 0; i < numberOfPriorities; i++) {
			lists[i] = new ManagingPositionableList<>();
		}
	}

	public void insert(T object) {
		lists[object.getPriority().ordinal()].insert(object);
	}

	public T getObjectAt(ShortPoint2D position, P priority) {
		T object = lists[priority.ordinal()].getObjectAt(position);
		if (object != null) {
			return object;
		}
		return null;
	}

	public T getObjectCloseTo(ShortPoint2D position, P minimumIncludedPriority) {
		T closestObject = null;
		for (int i = lists.length - 1; i >= minimumIncludedPriority.ordinal(); i--) {
			T object = lists[i].getObjectCloseTo(position);
			if (closestObject == null || (object != null && MathUtils.squareHypot(object.getPosition(), position) < MathUtils.squareHypot(closestObject.getPosition(), position))) {
				closestObject = object;
			}
		}
		return closestObject;
	}

	public void remove(T offer) {
		lists[offer.getPriority().ordinal()].remove(offer);
	}

	public void moveObjectsAtPositionTo(ShortPoint2D position, PrioritizedPositionableList<P, T> otherList, Consumer<T> movedVisitor) {
		for (int i = lists.length - 1; i >= 0; i--) {
			lists[i].moveObjectsAtPositionTo(position, otherList.lists[i], movedVisitor);
		}
	}

	public void moveAll(PrioritizedPositionableList<P, T> otherList, Consumer<T> movedVisitor) {
		for (int i = lists.length - 1; i >= 0; i--) {
			lists[i].moveAll(otherList.lists[i], movedVisitor);
		}
	}

	public void updatePriorityAt(ShortPoint2D position, P newPriority) {
		int newPriorityIndex = newPriority.ordinal();

		for (int i = 0; i < lists.length; i++) {
			if (i != newPriorityIndex) {
				T foundObject = lists[i].removeObjectAt(position);
				if (foundObject != null) {
					foundObject.updatePriority(newPriority);
					lists[newPriorityIndex].insert(foundObject);
				}
			}
		}
	}

	public boolean isEmpty(P minimumIncludedPriority) {
		for (int i = lists.length - 1; i >= minimumIncludedPriority.ordinal(); i--) {
			if (!lists[i].hasNoActive()) {
				return false;
			}
		}
		return true;
	}
}
