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

	private byte delayCtr = 0;
	private byte produced = 0;

	protected SpawnBuilding(EBuildingType type, Player player) {
		super(type, player);
	}

	@Override
	public final EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_DOOR;
	}

	@Override
	protected void constructionFinishedEvent() {
	}

	@Override
	protected final void subTimerEvent() {
		if (produced < getProduceLimit()) {

			if (delayCtr > 20) {
				NewMovable movableAtDoor = super.getGrid().getMovable(super.getDoor());
				if (movableAtDoor == null) {
					delayCtr = 0;

					movableAtDoor = new NewMovable(super.getGrid().getMovableGrid(), getMovableType(), getDoor(), super.getPlayer());
					produced++;
				}

				movableAtDoor.leavePosition();
			} else {
				delayCtr++;
			}
		}
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
