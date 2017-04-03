package jsettlers.logic.movable;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.logic.movable.interfaces.AbstractMovableGrid;
import jsettlers.logic.movable.interfaces.ILogicMovable;

/**
 * Created by jt-1 on 3/28/2017.
 */

public class GameFieldComponent extends Component {
    private static final HashMap<Integer, ILogicMovable> movablesByID = Movable.movablesByID;
    private static final ConcurrentLinkedQueue<ILogicMovable> allMovables = Movable.allMovables;
    private final AbstractMovableGrid movableGrid;

    public GameFieldComponent(AbstractMovableGrid grid) {
        this.movableGrid = grid;

    }

    public Queue<ILogicMovable> getAllMovables() {
        return allMovables;
    }

    public Map<Integer, ILogicMovable> getMovableMap() {
        return movablesByID;
    }

    public AbstractMovableGrid getMovableGrid() {
        return movableGrid;
    }
}
