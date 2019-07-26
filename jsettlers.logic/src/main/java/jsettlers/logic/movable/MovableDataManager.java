package jsettlers.logic.movable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.logic.movable.interfaces.ILogicMovable;

/**
 * @author homoroselaps
 */

public final class MovableDataManager {
	private static final HashMap<Integer, ILogicMovable>      movablesByID = new HashMap<>();
	private static final ConcurrentLinkedQueue<ILogicMovable> allMovables  = new ConcurrentLinkedQueue<>();

	private static int nextID = Integer.MIN_VALUE;

	/**
	 * Used for networking to identify movables over the network.
	 *
	 * @param id
	 *            id to be looked for
	 * @return returns the movable with the given ID<br>
	 *         or null if the id can not be found
	 */
	public static ILogicMovable getMovableByID(int id) {
		return movablesByID.get(id);
	}

	public static Collection<ILogicMovable> getAllMovables() {
		return allMovables;
	}

	public static void add(ILogicMovable movable) {
		movablesByID.put(movable.getID(), movable);
		allMovables.offer(movable);
	}

	public static void remove(ILogicMovable movable) {
		movablesByID.remove(movable.getID());
		allMovables.remove(movable);
	}

	public static void resetState() {
		allMovables.clear();
		movablesByID.clear();
		nextID = Integer.MIN_VALUE;
	}

	static int getNextID() {
		return nextID++;
	}

	public static void writeStaticState(ObjectOutputStream oos) throws IOException {
		oos.writeObject(movablesByID);
		oos.writeObject(allMovables);
		oos.writeInt(nextID);
	}

	@SuppressWarnings("unchecked")
	public static void readStaticState(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		movablesByID.clear();
		movablesByID.putAll((Map<? extends Integer, ? extends ILogicMovable>) ois.readObject());
		allMovables.clear();
		allMovables.addAll((Collection<? extends ILogicMovable>) ois.readObject());
		nextID = ois.readInt();
	}
}
