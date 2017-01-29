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
package jsettlers.logic.map.grid.partition;

import java8.util.function.Predicate;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.Tuple;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
	 * @param center
	 *            Position of the tower defining the center of it's area.
	 * @param radius
	 *            Radius of it's area.
	 * @param towerPredicate
	 *            Predicate specifying the towers that should be checked
	 * @return
	 */
	public List<Tuple<Integer, PartitionOccupyingTower>> getTowersInRange(ShortPoint2D center, int radius,
			Predicate<PartitionOccupyingTower> towerPredicate) {
		return StreamSupport.stream(this).filter(towerPredicate).map(tower -> {
			int sqDist = (int) MapCircle.getDistanceSquared(center.x, center.y, tower.position.x, tower.position.y);
			return new Tuple<>(sqDist, tower);
		}).filter(towerWithDistance -> {
			int maxDist = radius + towerWithDistance.e2.radius;
			return towerWithDistance.e1 <= (maxDist * maxDist);
		}).collect(Collectors.toList());
	}
}
