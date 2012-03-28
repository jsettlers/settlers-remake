package jsettlers.logic.newmovable;

import jsettlers.common.movable.EAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;

public class TestMovableStrategy extends NewMovableStrategy {
	private static final long serialVersionUID = -8014915507026812395L;

	public TestMovableStrategy(NewMovable movable) {
		super(movable);
	}

	int ctr = 0;

	@Override
	protected void action() {
		ctr++;
		if (ctr == 20) {
			super.playAction(EAction.TAKE, Constants.MOVABLE_TAKE_DROP_DURATION);
		} else if (ctr == 40) {
			ctr = 0;
			ShortPoint2D pos = super.getPosition();
			super.goToPos(new ShortPoint2D(70 - pos.getX(), 60 - pos.getY()));
		}
	}
}
