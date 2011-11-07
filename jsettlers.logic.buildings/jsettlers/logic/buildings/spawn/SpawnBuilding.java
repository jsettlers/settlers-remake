package jsettlers.logic.buildings.spawn;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.movable.Movable;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class SpawnBuilding extends Building {
	private static final long serialVersionUID = 7584783336566602225L;

	private byte delayCtr = 0;
	private int produced = 0;

	protected SpawnBuilding(EBuildingType type, byte player) {
		super(type, player);
	}

	@Override
	public boolean isOccupied() {
		return super.isConstructed();
	}

	@Override
	public EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_DOOR;
	}

	@Override
	protected void constructionFinishedEvent() {
	}

	@Override
	protected void subTimerEvent() {
		if (produced < getProduceLimit()) {

			if (delayCtr > 20) {
				if (super.grid.getMovable(super.getDoor()) == null) {
					delayCtr = 0;

					super.grid.placeNewMovable(getDoor(),
							new Movable(super.grid.getMovableGrid(), super.getDoor(), getMovableType(), super.getPlayer()));
					// Movable created = getDoor().getMovable();
					produced++;
				}
			} else {
				delayCtr++;
			}
		}
	}

	protected abstract EMovableType getMovableType();

	protected abstract int getProduceLimit();

	@Override
	public void stopOrStartWorking(boolean stop) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void positionedEvent(ISPosition2D pos) {
	}
}
