package jsettlers.logic.movable.strategies;

import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;
import networklib.synchronic.random.RandomSingleton;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class FleeStrategy extends MovableStrategy {
	private static final long serialVersionUID = -7693464085159449304L;
	private int searchesCounter = 0;
	private boolean turnNextTime;

	public FleeStrategy(Movable movable) {
		super(movable);
	}

	@Override
	protected void action() {
		ShortPoint2D position = super.getPos();
		if (!super.isValidPosition(position)) {
			if (searchesCounter > 120) {
				super.getMovable().kill();
				return;
			}

			if (super.preSearchPath(true, position.x, position.y, Constants.MOVABLE_FLEE_TO_VALID_POSITION_RADIUS, ESearchType.VALID_FREE_POSITION)) {
				super.followPresearchedPath();
			} else {
				EDirection currentDirection = super.getMovable().getDirection();
				EDirection newDirection;
				if (turnNextTime || RandomSingleton.nextF() < 0.10) {
					turnNextTime = false;
					newDirection = currentDirection.getNeighbor(RandomSingleton.getInt(-1, 1));
				} else {
					newDirection = currentDirection;
				}

				ShortPoint2D newPos = newDirection.getNextHexPoint(position);

				if (super.getStrategyGrid().isFreePosition(newPos)) {
					super.forceGoInDirection(newDirection);
				} else {
					super.lookInDirection(newDirection);
					turnNextTime = true;
				}
			}

			searchesCounter++;
		} else {
			super.convertTo(super.getMovable().getMovableType());
		}
	}

	@Override
	protected boolean checkPathStepPreconditions(ShortPoint2D pathTarget, int step) {
		return step <= 2;
	}
}
