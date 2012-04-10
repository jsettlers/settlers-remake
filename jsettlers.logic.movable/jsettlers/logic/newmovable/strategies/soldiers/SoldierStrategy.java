package jsettlers.logic.newmovable.strategies.soldiers;

import jsettlers.common.movable.EMovableType;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.newmovable.NewMovableStrategy;

public class SoldierStrategy extends NewMovableStrategy {
	private static final long serialVersionUID = 5246120883607071865L;
	private final EMovableType movableType;

	public SoldierStrategy(NewMovable movable, EMovableType movableType) {
		super(movable);
		this.movableType = movableType;
	}

	@Override
	protected void action() {
	}

}
