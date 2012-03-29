package jsettlers.logic.newmovable;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.newmovable.interfaces.IStrategyGrid;
import jsettlers.logic.newmovable.strategies.BearerMovableStrategy;
import jsettlers.logic.newmovable.strategies.TestMovableStrategy;

public abstract class NewMovableStrategy implements Serializable {
	private static final long serialVersionUID = 3135655342562634378L;

	private final NewMovable movable;

	protected NewMovableStrategy(NewMovable movable) {
		this.movable = movable;
		// TODO Auto-generated constructor stub
	}

	public static NewMovableStrategy getStrategy(NewMovable movable, EMovableType movableType) {
		switch (movableType) {
		case TEST_MOVABLE:
			return new TestMovableStrategy(movable);
		case BEARER:
			return new BearerMovableStrategy(movable);

		default:
			return null;
		}
	}

	protected abstract void action();

	protected final void convertTo(EMovableType movableType) {
		movable.convertTo(movableType);
	}

	protected final EMaterialType setMaterial(EMaterialType materialType) {
		return movable.setMaterial(materialType);
	}

	protected final void playAction(EAction movableAction, float duration) { // TODO @Andreas : rename EAction to EMovableAction
		movable.playAction(movableAction, duration);
	}

	protected final void lookInDirection(EDirection direction) {
		movable.lookInDirection(direction);
	}

	protected final boolean goToPos(ShortPoint2D targetPos) {
		return movable.goToPos(targetPos);
	}

	protected final IStrategyGrid getStrategyGrid() {
		return movable.getStrategyGrid();
	}

	public final ShortPoint2D getPos() {
		return movable.getPos();
	}

	/**
	 * Checks preconditions before the next path step can be gone.
	 * 
	 * @return true if the path should be continued<br>
	 *         false if it must be stopped.
	 */
	protected boolean checkPathStepPreconditions() {
		return true;
	}
}
