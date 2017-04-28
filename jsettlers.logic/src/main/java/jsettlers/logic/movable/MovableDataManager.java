package jsettlers.logic.movable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.logic.movable.interfaces.ILogicMovable;

/**
 * @author homoroselaps
 */

public final class MovableDataManager {
    private static HashMap<Integer, ILogicMovable> movablesByID;
    private static ConcurrentLinkedQueue<ILogicMovable> allMovables;
    private static Integer nextID = Integer.MIN_VALUE;

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

    public static ConcurrentLinkedQueue<ILogicMovable> allMovables() {
        if (allMovables == null) allMovables  = new ConcurrentLinkedQueue<>();
        return allMovables;
    }

    public static Map<Integer, ILogicMovable> movablesByID() {
        if (movablesByID == null) movablesByID = new HashMap<Integer, ILogicMovable>();
        return movablesByID;
    }

    public static void resetState() {
        if (allMovables != null) allMovables.clear();
        if (movablesByID != null) movablesByID.clear();
        nextID = Integer.MIN_VALUE;
    }

    public static void setNextID(int id) {
        nextID = Math.max(nextID, id);
    }

    public static int getNextID() {
        return nextID++;
    }
}
