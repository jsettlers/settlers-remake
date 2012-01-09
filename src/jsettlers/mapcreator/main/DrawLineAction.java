package jsettlers.mapcreator.main;

import jsettlers.common.position.ISPosition2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;

public class DrawLineAction extends Action {

	private final ISPosition2D start;
	private final ISPosition2D end;
	private final double uidy;

	public DrawLineAction(ISPosition2D start, ISPosition2D end, double uidy) {
	    super(EActionType.UNSPECIFIED);
		this.start = start;
		this.end = end;
		this.uidy = uidy;
    }
	
	public ISPosition2D getStart() {
	    return start;
    }
	
	public ISPosition2D getEnd() {
	    return end;
    }
	
	public double getUidy() {
	    return uidy;
    }

}
