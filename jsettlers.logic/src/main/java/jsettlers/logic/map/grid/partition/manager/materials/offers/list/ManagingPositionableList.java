/*******************************************************************************
 * Copyright (c) 2017
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
package jsettlers.logic.map.grid.partition.manager.materials.offers.list;

import java.util.Iterator;

import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.MathUtils;
import jsettlers.logic.map.grid.partition.manager.datastructures.PositionableList;

/**
 * This is a data structure for storing and retrieving objects at given positions.<br>
 * It is also possible to find the nearest object around a given position.
 *
 * @param <T>
 * @author Andreas Eberle
 */
public class ManagingPositionableList<T extends ILocatable & IListManageable> extends PositionableList<T> {

	@Override
	protected T getObjectCloseTo(ShortPoint2D position) {
		int bestDistance = Integer.MAX_VALUE;
		T currBest = null;

		for (Iterator<T> iterator = data.iterator(); iterator.hasNext(); ) {
			T currEntry = iterator.next();

			if (currEntry.canBeRemoved()) { // remove old entries no longer needed
				iterator.remove();

			} else if (currEntry.isActive()) { // only use the active ones
				int currDist = MathUtils.squareHypot(position, currEntry.getPosition());

				if (bestDistance > currDist) {
					bestDistance = currDist;
					currBest = currEntry;
				}
			}
		}
		return currBest;
	}

	public boolean hasNoActive() {
		for (Iterator<T> iterator = data.iterator(); iterator.hasNext(); ) {
			T datum = iterator.next();

			if (datum.canBeRemoved()) {
				iterator.remove();

			} else if (datum.isActive()) {
				return false;
			}
		}
		return true;
	}
}
