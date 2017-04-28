package jsettlers.logic.movable.components;

import java.util.Map;
import java.util.Queue;

import jsettlers.logic.movable.MovableDataManager;
import jsettlers.logic.movable.interfaces.AbstractMovableGrid;
import jsettlers.logic.movable.interfaces.ILogicMovable;

/**
 * @author homoroselaps
 */

public class GameFieldComponent extends Component {
    private static final long serialVersionUID = 476680901281177567L;
    private final AbstractMovableGrid movableGrid;

    public GameFieldComponent(AbstractMovableGrid grid) {
        this.movableGrid = grid;
    }

    public Queue<ILogicMovable> getAllMovables() {
        return MovableDataManager.allMovables();
    }

    public Map<Integer, ILogicMovable> getMovableMap() {
        return MovableDataManager.movablesByID();
    }

    public AbstractMovableGrid getMovableGrid() {
        return movableGrid;
    }
}
