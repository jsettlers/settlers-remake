package jsettlers.mapcreator.main;

import jsettlers.common.position.ISPosition2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;

public class EndDrawingAction extends Action {

	private final ISPosition2D pos;

	public EndDrawingAction(ISPosition2D pos) {
	    super(EActionType.UNSPECIFIED);
		this.pos = pos;
    }
	
	public ISPosition2D getPos() {
	    return pos;
    }

}
