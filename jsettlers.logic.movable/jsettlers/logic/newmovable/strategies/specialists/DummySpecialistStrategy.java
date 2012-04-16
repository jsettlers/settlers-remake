package jsettlers.logic.newmovable.strategies.specialists;

import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.newmovable.NewMovableStrategy;

public final class DummySpecialistStrategy extends NewMovableStrategy {
	private static final long serialVersionUID = -1359250497501671076L;

	public DummySpecialistStrategy(NewMovable movable) {
		super(movable);
	}

	@Override
	protected void action() {
	}

}
