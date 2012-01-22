package jsettlers.graphics.action;

import jsettlers.common.position.ISPosition2D;

/**
 * This is a select action that states that the user selected a position on the
 * map.
 * 
 * @author michael
 */
public class SelectAction extends Action {

	private final ISPosition2D position;

	/**
	 * Creates a new select action.
	 * 
	 * @param position
	 *            The position the user wants to select.
	 */
	public SelectAction(ISPosition2D position) {
		super(EActionType.SELECT_POINT);
		this.position = position;
	}

	public SelectAction(ISPosition2D position, EActionType action) {
		super(action);
		this.position = position;
    }

	/**
	 * Gets the position on the map.
	 * 
	 * @return The position the user selected.
	 */
	public ISPosition2D getPosition() {
		return this.position;
	}
}
