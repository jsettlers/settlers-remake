package jsettlers.graphics.action;

import jsettlers.common.position.ShortPoint2D;

/**
 * This is a select action that states that the user selected a position on the
 * map.
 * 
 * @author michael
 */
public class SelectAction extends Action {

	private final ShortPoint2D position;

	/**
	 * Creates a new select action.
	 * 
	 * @param position
	 *            The position the user wants to select.
	 */
	public SelectAction(ShortPoint2D position) {
		super(EActionType.SELECT_POINT);
		this.position = position;
	}

	public SelectAction(ShortPoint2D position, EActionType action) {
		super(action);
		this.position = position;
    }

	/**
	 * Gets the position on the map.
	 * 
	 * @return The position the user selected.
	 */
	public ShortPoint2D getPosition() {
		return this.position;
	}
}
