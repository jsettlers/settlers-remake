package jsettlers.logic.map.newGrid.partition.manager.datastructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.datastructures.PositionableHashMap.IAcceptor;

/**
 * This is a data structure for storing and retrieving objects at given positions.<br>
 * It is also possible to find the nearest object arround a given position.
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
	 * Finds the object that's closest to the given position and removes it.
	 * 
	 * @param position
	 *            position to be used to find the nearest accepted neighbor around it.
	 * @param acceptor
	 *            if acceptor != null => the result is accepted by the acceptor. <br>
	 *            if result == null every entry is accepted.
	 * @return accepted object that's nearest to position
	 */
	public T removeObjectNextTo(ShortPoint2D position, IAcceptor<T> acceptor) {
		float bestDistance = Float.MAX_VALUE;
		T currBest = null;

		for (T currEntry : data) {
			if (acceptor != null && !acceptor.isAccepted(currEntry))
				continue;

			ShortPoint2D currPosition = currEntry.getPos();
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
	public String toString() {
		return data.toString();
	}

	public void addAll(PositionableList<T> joblessBearer) {
		this.data.addAll(joblessBearer.data);
	}

	public void remove(T object) {
		this.data.remove(object);
	}

}
