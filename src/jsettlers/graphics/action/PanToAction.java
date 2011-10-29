package jsettlers.graphics.action;

import jsettlers.common.position.ISPosition2D;

/**
 * The action that states that the ui should pan to a given point.
 * @author michael
 *
 */
public class PanToAction extends Action {

	private final ISPosition2D center;

	public PanToAction(ISPosition2D center) {
	    super(EActionType.PAN_TO);
		this.center = center;
    }

	/**
	 * Gets the point that should be at the center of the screen.
	 * @return The point.
	 */
	public ISPosition2D getCenter() {
	    return center;
    }

}
