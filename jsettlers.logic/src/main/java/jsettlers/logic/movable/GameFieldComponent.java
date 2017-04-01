package jsettlers.logic.movable;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.logic.movable.interfaces.AbstractMovableGrid;

/**
 * Created by jt-1 on 3/28/2017.
 */

public class GameFieldComponent extends Component {
    private static final HashMap<Integer, Movable> movablesByID = new HashMap<Integer, Movable>();
    private static final ConcurrentLinkedQueue<Movable> allMovables = new ConcurrentLinkedQueue<Movable>();
    private final AbstractMovableGrid movableGrid;

    public GameFieldComponent(AbstractMovableGrid grid) {
        this.movableGrid = grid;
    }

    public Queue<Movable> getAllMovables() {
        return allMovables;
    }

    public Map<Integer, Movable> getMovableMap() {
        return movablesByID;
    }

    public AbstractMovableGrid getMovableGrid() {
        return movableGrid;
    }
}
