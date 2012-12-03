package jsettlers.graphics.action;

import jsettlers.common.position.ShortPoint2D;

/**
 * This action states that the user wants something to move to the given
 * position.
 *
 * @author michael
 */
public class PointAction extends Action {

	private final ShortPoint2D position;

	/**
	 * Creates a new moveto aciton.
	 *
	 * @param position
	 *            The position the user clicked at.
	 */
	public PointAction(EActionType type, ShortPoint2D position) {
		super(type);
		this.position = position;
	}

	/**
	 * Gets the position on the map the user wants to move the unit(s) to.
	 *
	 * @return The position.
	 */
	public ShortPoint2D getPosition() {
		return this.position;
	}

	/**
	 * Defines it the units should start working when reaching their
	 * destination.
	 *
	 * @return a boolean value.
	 */
	public boolean startWorking() {
		return false;
	}
}
