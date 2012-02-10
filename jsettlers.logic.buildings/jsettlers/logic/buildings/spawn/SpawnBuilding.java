package jsettlers.logic.buildings.spawn;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.movable.Movable;

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

	protected SpawnBuilding(EBuildingType type, byte player) {
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
				if (super.getGrid().getMovable(super.getDoor()) == null) {
					delayCtr = 0;

					super.getGrid().placeNewMovable(getDoor(),
							new Movable(super.getGrid().getMovableGrid(), super.getDoor(), getMovableType(), super.getPlayer()));
					// Movable created = getDoor().getMovable();
					produced++;
				}
			} else {
				delayCtr++;
			}
		}
	}

	protected abstract EMovableType getMovableType();

	protected abstract byte getProduceLimit();

	@Override
	public final void stopOrStartWorking(boolean stop) {
	}

	@Override
	protected void positionedEvent(ISPosition2D pos) {
	}

	@Override
	public final boolean isOccupied() {
		return true;
	}
}
