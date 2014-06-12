package jsettlers.logic.buildings.spawn;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.player.Player;

/**
 * Abstract parent class for buildings that spawn new movables.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class SpawnBuilding extends Building {
	private static final long serialVersionUID = 7584783336566602225L;

	private static final int PRODUCE_PERIOD = 2000;
	private byte produced = 0;

	protected SpawnBuilding(EBuildingType type, Player player) {
		super(type, player);
	}

	@Override
	public final EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_DOOR;
	}

	@Override
	protected int constructionFinishedEvent() {
		return PRODUCE_PERIOD;
	}

	@Override
	protected final int subTimerEvent() {
		int rescheduleDelay;
		NewMovable movableAtDoor = super.getGrid().getMovable(super.getDoor());

		if (movableAtDoor == null) {
			movableAtDoor = new NewMovable(super.getGrid().getMovableGrid(), getMovableType(), getDoor(), super.getPlayer());
			produced++;

			if (produced < getProduceLimit()) {
				rescheduleDelay = PRODUCE_PERIOD;
			} else {
				rescheduleDelay = -1; // remove from scheduling
			}
		} else {
			rescheduleDelay = 100; // door position wasn't free, so check more often
		}

		movableAtDoor.leavePosition();

		return rescheduleDelay;
	}

	protected abstract EMovableType getMovableType();

	protected abstract byte getProduceLimit();

	@Override
	protected void positionedEvent(ShortPoint2D pos) {
	}

	@Override
	public final boolean isOccupied() {
		return true;
	}
}
