package jsettlers.logic.movable.strategies.specialists;

import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public final class DummySpecialistStrategy extends MovableStrategy {
	private static final long serialVersionUID = -1359250497501671076L;

	public DummySpecialistStrategy(Movable movable) {
		super(movable);
	}

	@Override
	protected void action() {
	}

	@Override
	protected boolean isMoveToAble() {
		return true;
	}
}
