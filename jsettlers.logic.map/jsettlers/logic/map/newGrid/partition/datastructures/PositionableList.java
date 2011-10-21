package jsettlers.logic.map.newGrid.partition.datastructures;

import java.util.ArrayList;
import java.util.Iterator;

import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ISPosition2D;

/**
 * This is a data structure for storing and retrieving objects at given positions.<br>
 * It is also possible to find the nearest object arround a given position.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 */
public class PositionableList<T extends ILocatable> implements Iterable<T> {

	private ArrayList<T> data;

	public PositionableList() {
		data = new ArrayList<T>();
	}

	public void set(T object) {
		data.add(object);
	}

	/**
	 * Finds the object that's closest to the given position and removes it.
	 * 
	 * @param position
	 *            position to be used to find the nearest accepted neighbor around it.
	 * @return accepted object that's nearest to position
	 */
	public T removeObjectNextTo(ISPosition2D position) {
		float bestDistance = Float.MAX_VALUE;
		T currBest = null;

		for (T currEntry : data) {
			ISPosition2D currPosition = currEntry.getPos();
			float currDist = (float) Math.hypot(position.getX() - currPosition.getX(), position.getY() - currPosition.getY());

			if (bestDistance > currDist) {
				bestDistance = currDist;
				currBest = currEntry;
			}
		}

		if (currBest != null)
			data.remove(currBest);

		return currBest;
	}

	@Override
	public Iterator<T> iterator() {
		return data.iterator();
	}

	public T removeObjectAt(ISPosition2D position) {
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

}
