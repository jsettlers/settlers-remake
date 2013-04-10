package jsettlers.logic.buildings.others;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.player.Player;

/**
 * This is a stock building that can store materials.
 * 
 * @author Andreas Eberle
 * 
 */
public final class StockBuilding extends Building {
	private static final long serialVersionUID = 1L;

	public StockBuilding(Player player) {
		super(EBuildingType.STOCK, player);
	}

	@Override
	public boolean isOccupied() {
		return false;
	}

	@Override
	public void stopOrStartWorking(boolean stop) {
	}

	@Override
	protected void positionedEvent(ShortPoint2D pos) {
	}

	@Override
	protected void subTimerEvent() {
	}

	@Override
	protected void constructionFinishedEvent() {
	}

	@Override
	protected EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_DOOR;
	}

}
