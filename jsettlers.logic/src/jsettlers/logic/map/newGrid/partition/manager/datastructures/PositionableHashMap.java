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
package jsettlers.logic.map.newGrid.partition.manager.datastructures;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

import jsettlers.algorithms.queue.ITypeAcceptor;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.MathUtils;

/**
 * This is a data structure for storing and retrieving objects at given positions.<br>
 * It is also possible to find the nearest object arround a given position.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 */
public class PositionableHashMap<T> implements Iterable<T>, Serializable {
	private static final long serialVersionUID = 5748990341167824377L;

	private Hashtable<ShortPoint2D, T> data;

	public PositionableHashMap() {
		data = new Hashtable<ShortPoint2D, T>();
	}

	public void set(ShortPoint2D position, T object) {
		data.put(position, object);
	}

	public T getObjectAt(ShortPoint2D position) {
		return data.get(position);
	}

	public T removeObjectAt(ShortPoint2D position) {
		return data.remove(position);
	}

	/**
	 * Finds the object that's closest to the given position and that's accepted by the given acceptor.
	 * 
	 * @param position
	 *            position to be used to find the nearest accepted neighbor around it.
	 * @param acceptor
	 *            acceptor checking if the given object can be accepted.<br>
	 *            if acceptor == null, every object will be accepted.
	 * @return accepted object that's nearest to position
	 */
	public T getObjectNextTo(ShortPoint2D position, ITypeAcceptor<T> acceptor) {
		int bestDistance = Integer.MAX_VALUE;
		T currBest = null;

		for (Entry<ShortPoint2D, T> currEntry : data.entrySet()) {
			if (acceptor == null || acceptor.accepts(currEntry.getValue())) {
				ShortPoint2D currPosition = currEntry.getKey();
				int currDist = MathUtils.squareHypot(position, currPosition);

				if (bestDistance > currDist) {
					bestDistance = currDist;
					currBest = currEntry.getValue();
				}
			}
		}

		return currBest;
	}

	@Override
	public Iterator<T> iterator() {
		return data.values().iterator();
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public void addAll(PositionableHashMap<T> materialOffers) {
		for (Entry<ShortPoint2D, T> entry : materialOffers.data.entrySet()) {
			// needed to track the count of elements
			set(entry.getKey(), entry.getValue());
		}
	}

}
