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
import java.util.ArrayList;
import java.util.Iterator;

import jsettlers.algorithms.queue.ITypeAcceptor;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.MathUtils;

/**
 * This is a data structure for storing and retrieving objects at given positions.<br>
 * It is also possible to find the nearest object around a given position.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 */
public class PositionableList<T extends ILocatable> implements Iterable<T>, Serializable {
	private static final long serialVersionUID = 414099060331344505L;

	private ArrayList<T> data;

	public PositionableList() {
		data = new ArrayList<T>();
	}

	public void insert(T object) {
		data.add(object);
	}

	/**
	 * Finds the object that's closest to the given position and removes it.
	 * 
	 * @param position
	 *            position to be used to find the nearest accepted neighbor around it.
	 * @return object that's nearest to position
	 */
	public T removeObjectNextTo(ShortPoint2D position) {
		return removeObjectNextTo(position, null);
	}

	@Override
	public Iterator<T> iterator() {
		return data.iterator();
	}

	public T removeObjectAt(ShortPoint2D position) {
		Iterator<T> iter = data.iterator();
		while (iter.hasNext()) {
			T curr = iter.next();
			if (curr.getPos().equals(position)) {
				iter.remove();
				return curr;
			}
		}
		return null;
	}

	/**
	 * Returns the first object found at the given position or null.
	 * 
	 * @param position
	 *            The position to look for.
	 * @return Returns the found object at the given position or null if no object has been found.
	 */
	public T getObjectAt(ShortPoint2D position) {
		for (T curr : data) {
			if (curr.getPos().equals(position)) {
				return curr;
			}
		}
		return null;
	}

	/**
	 * Finds the object that's closest to the given position and removes it.
	 * 
	 * @param position
	 *            position to be used to find the nearest accepted neighbor around it.
	 * @param acceptor
	 *            if acceptor != null => the result is accepted by the acceptor. <br>
	 *            if result == null every entry is accepted.
	 * @return accepted object that's nearest to position
	 */
	public T removeObjectNextTo(ShortPoint2D position, ITypeAcceptor<T> acceptor) {
		T currBest = getObjectCloseTo(position, acceptor);

		if (currBest != null)
			data.remove(currBest);

		return currBest;
	}

	private T getObjectCloseTo(ShortPoint2D position, ITypeAcceptor<T> acceptor) {
		int bestDistance = Integer.MAX_VALUE;
		T currBest = null;

		for (T currEntry : data) {
			if (acceptor != null && !acceptor.accepts(currEntry))
				continue;

			ShortPoint2D currPosition = currEntry.getPos();
			int currDist = MathUtils.squareHypot(position, currPosition);

			if (bestDistance > currDist) {
				bestDistance = currDist;
				currBest = currEntry;
			}
		}
		return currBest;
	}

	public T getObjectCloseTo(ShortPoint2D position) {
		return getObjectCloseTo(position, null);
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public void addAll(PositionableList<T> otherList) {
		this.data.addAll(otherList.data);
	}

	public void remove(T object) {
		this.data.remove(object);
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public void moveObjectsAtPositionTo(ShortPoint2D position, PositionableList<T> newList, IMovedVisitor<? super T> movedVisitor) {
		Iterator<T> iter = data.iterator();
		while (iter.hasNext()) {
			T curr = iter.next();
			if (curr.getPos().equals(position)) {
				iter.remove();
				movedVisitor.visit(curr);
				newList.data.add(curr);
			}
		}
	}

	public int size() {
		return data.size();
	}

	public interface IMovedVisitor<T> {
		void visit(T moved);
	}
}
