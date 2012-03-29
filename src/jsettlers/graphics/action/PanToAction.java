package jsettlers.graphics.action;

import jsettlers.common.position.ShortPoint2D;

/**
 * The action that states that the ui should pan to a given point.
 * @author michael
 *
 */
public class PanToAction extends Action {

	private final ShortPoint2D center;

	public PanToAction(ShortPoint2D center) {
	    super(EActionType.PAN_TO);
		this.center = center;
    }

	/**
	 * Gets the point that should be at the center of the screen.
	 * @return The point.
	 */
	public ShortPoint2D getCenter() {
	    return center;
    }

}
