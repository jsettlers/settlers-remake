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
package jsettlers.logic.map.grid.partition.manager.datastructures;

import java.io.Serializable;

import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.manager.datastructures.PositionableList.IMovedVisitor;

/**
 * Created by Andreas Eberle on 23.08.2016.
 */
public class PrioritizedPositionableList<P extends Enum, T extends ILocatable> implements Serializable {
	private final PositionableList<T>[] lists;

	public PrioritizedPositionableList(int numberOfPriorities) {
		lists = new PositionableList[numberOfPriorities];
		for (int i = 0; i < numberOfPriorities; i++) {
			lists[i] = new PositionableList<>();
		}
	}

	public void insert(P priority, T object) {
		lists[priority.ordinal()].insert(object);
	}

	public T getObjectAt(ShortPoint2D position, P priority) {
		T object = lists[priority.ordinal()].getObjectAt(position);
		if (object != null) {
			return object;
		}
		return null;
	}

	public int size(P minimumIncludedPriority) {
		int size = 0;
		for (int i = lists.length - 1; i >= minimumIncludedPriority.ordinal(); i--) {
			size += lists[i].size();
		}
		return size;
	}

	public boolean isEmpty(P minimumIncludedPriority) {
		return size(minimumIncludedPriority) <= 0;
	}

	public T getObjectCloseTo(ShortPoint2D position, P minimumIncludedPriority) {
		for (int i = lists.length - 1; i >= minimumIncludedPriority.ordinal(); i--) {
			T object = lists[i].getObjectCloseTo(position);
			if (object != null) {
				return object;
			}
		}
		return null;
	}

	public void remove(T offer) {
		for (int i = lists.length - 1; i >= 0; i--) {
			lists[i].remove(offer);
		}
	}

	public void moveObjectsAtPositionTo(ShortPoint2D position, PrioritizedPositionableList<P, T> otherList, IMovedVisitor<T> movedVisitor) {
		for (int i = lists.length - 1; i >= 0; i--) {
			lists[i].moveObjectsAtPositionTo(position, otherList.lists[i], movedVisitor);
		}
	}

	public void addAll(PrioritizedPositionableList<P, T> otherList) {
		for (int i = lists.length - 1; i >= 0; i--) {
			lists[i].addAll(otherList.lists[i]);
		}
	}

	public void updatePriorityAt(ShortPoint2D position, P newPriority) {
		int newPriorityIndex = newPriority.ordinal();

		for (int i = 0; i < lists.length; i++) {
			if (i != newPriorityIndex) {
				T foundObject = lists[i].removeObjectAt(position);
				if (foundObject != null) {
					lists[newPriorityIndex].insert(foundObject);
				}
			}
		}
	}
}
