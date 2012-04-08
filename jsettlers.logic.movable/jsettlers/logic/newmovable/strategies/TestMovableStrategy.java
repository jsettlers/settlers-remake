package jsettlers.logic.newmovable.strategies;

import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.newmovable.NewMovableStrategy;

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
			super.playAction(EAction.ACTION1, 0.8f);
		} else if (ctr == 40) {
			super.forceGoInDirection(EDirection.EAST);
		} else if (ctr == 60) {
			ctr = 0;
			ShortPoint2D pos = super.getPos();
			super.goToPos(new ShortPoint2D(80 - pos.getX(), 70 - pos.getY()));
		}
	}
}
