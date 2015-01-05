package jsettlers.mapcreator.main.action;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;

public class StartDrawingAction extends Action {

	private final ShortPoint2D pos;

	public StartDrawingAction(ShortPoint2D pos) {
		super(EActionType.UNSPECIFIED);
		this.pos = pos;
	}

	public ShortPoint2D getPos() {
		return pos;
	}

}
