package jsettlers.graphics.action;

import jsettlers.common.position.ISPosition2D;

public class PanToAction extends Action {

	private final ISPosition2D center;

	public PanToAction(ISPosition2D center) {
	    super(EActionType.PAN_TO);
		this.center = center;
    }

	public ISPosition2D getCenter() {
	    return center;
    }

}
