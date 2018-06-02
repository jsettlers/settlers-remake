package jsettlers.common.action;

import jsettlers.common.position.ShortPoint2D;

/**
 * Action for {@link EActionType#MOVE_TO}
 * @author Michael Zangl
 */
public class MoveToAction extends PointAction {

	private final EMoveToType moveToType;

	public MoveToAction(EMoveToType moveToType, ShortPoint2D position) {
		super(EActionType.MOVE_TO, position);
		this.moveToType = moveToType;
	}
	
	public EMoveToType getMoveToType() {
		return moveToType;
	}
}
