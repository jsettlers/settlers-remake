package jsettlers.logic.map.newGrid.partition;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

import jsettlers.common.position.ISPosition2D;

/**
 * This is a data structure for storing and retrieving objects at given positions.<br>
 * It is also possible to find the nearest object arround a given position.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 */
public class PositionableDatastructure<T> implements Iterable<T> {

	private Hashtable<ISPosition2D, T> data;

	public PositionableDatastructure() {
		data = new Hashtable<ISPosition2D, T>();
	}

	public void set(ISPosition2D position, T object) {
		data.put(position, object);
	}

	public T getObjectAt(ISPosition2D position) {
		return data.get(position);
	}

	public T removeObjectAt(ISPosition2D position) {
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
	public T getObjectNextTo(ISPosition2D position, IAcceptor<T> acceptor) {
		float bestDistance = Float.MAX_VALUE;
		T currBest = null;

		for (Entry<ISPosition2D, T> currEntry : data.entrySet()) {
			if (acceptor == null || acceptor.isAccepted(currEntry.getValue())) {
				ISPosition2D currPosition = currEntry.getKey();
				float currDist = (float) Math.hypot(position.getX() - currPosition.getX(), position.getY() - currPosition.getY());

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

	/**
	 * This interface can be used to specify objects that should be accepted and objects that shouldn't.
	 * 
	 * @author Andreas Eberle
	 * 
	 * @param <T>
	 */
	public static interface IAcceptor<T> {
		public boolean isAccepted(T object);
	}
}
