package jsettlers.logic.movable.components;

import jsettlers.logic.movable.MovableDataManager;
import jsettlers.logic.movable.interfaces.AbstractMovableGrid;
import jsettlers.logic.movable.interfaces.ILogicMovable;

/**
 * @author homoroselaps
 */
public class GameFieldComponent extends Component {
	private static final long serialVersionUID = 476680901281177567L;

	public final AbstractMovableGrid movableGrid;

	public GameFieldComponent(AbstractMovableGrid grid) {
		this.movableGrid = grid;
	}

	void addNewMovable(ILogicMovable movable) {
		MovableDataManager.add(movable);
	}

	void removeMovable(ILogicMovable movable) {
		MovableDataManager.remove(movable);
	}
}
