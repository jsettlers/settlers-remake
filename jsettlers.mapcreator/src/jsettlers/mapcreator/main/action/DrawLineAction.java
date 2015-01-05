package jsettlers.mapcreator.main.action;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;

public class DrawLineAction extends Action {

	private final ShortPoint2D start;
	private final ShortPoint2D end;
	private final double uidy;

	public DrawLineAction(ShortPoint2D start, ShortPoint2D end, double uidy) {
		super(EActionType.UNSPECIFIED);
		this.start = start;
		this.end = end;
		this.uidy = uidy;
	}

	public ShortPoint2D getStart() {
		return start;
	}

	public ShortPoint2D getEnd() {
		return end;
	}

	public double getUidy() {
		return uidy;
	}

}
