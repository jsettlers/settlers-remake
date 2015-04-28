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
package jsettlers.logic.map.newGrid.partition;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.Tuple;

/**
 * A data structure to store the towers that occupy areas on the {@link PartitionsGrid}.
 * 
 * @author Andreas Eberle
 * 
 */
final class PartitionOccupyingTowerList extends LinkedList<PartitionOccupyingTower> {
	private static final long serialVersionUID = -2459360464464831879L;

	/**
	 * Returns the {@link PartitionOccupyingTower} object at the given position if it exists or null.
	 * 
	 * @param position
	 *            The position the expected {@link PartitionOccupyingTower} is located.
	 * @return
	 * 
	 */
	public PartitionOccupyingTower removeAt(ShortPoint2D position) {
		Iterator<PartitionOccupyingTower> iter = iterator();
		while (iter.hasNext()) {
			PartitionOccupyingTower curr = iter.next();
			if (curr.position.equals(position)) {
				iter.remove();
				return curr;
			}
		}
		return null;
	}

	/**
	 * Returns the {@link PartitionOccupyingTower} objects with areas that intersect the area specified by the given position and radius.
	 * 
	 * @param position
	 *            Position of the tower defining the center of it's area.
	 * @param radius
	 *            Radius of it's area.
	 * @return
	 */
	public List<Tuple<Integer, PartitionOccupyingTower>> getTowersInRange(ShortPoint2D center, int radius) {
		LinkedList<Tuple<Integer, PartitionOccupyingTower>> result = new LinkedList<Tuple<Integer, PartitionOccupyingTower>>();

		for (PartitionOccupyingTower curr : this) {
			int sqDist = (int) MapCircle.getDistanceSquared(center.x, center.y, curr.position.x, curr.position.y);
			int maxDist = radius + curr.radius;

			if (sqDist <= (maxDist * maxDist)) {
				result.add(new Tuple<Integer, PartitionOccupyingTower>(sqDist, curr));
			}
		}

		return result;
	}
}
