package jsettlers.logic.movable.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.interfaces.AbstractMovableGrid;
import jsettlers.logic.movable.interfaces.ILogicMovable;

/**
 * @author homoroselaps
 */

public class GameFieldComponent extends Component {
    private static final long serialVersionUID = 476680901281177567L;
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
