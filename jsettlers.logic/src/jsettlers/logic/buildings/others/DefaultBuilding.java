package jsettlers.logic.buildings.others;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.player.Player;

/**
 * This is a default building. It can be used for every building type that has no real function.
 * 
 * @author Andreas Eberle
 * 
 */
public final class DefaultBuilding extends Building {
	private static final long serialVersionUID = 1L;

	public DefaultBuilding(EBuildingType buildingType, Player player) {
		super(buildingType, player);
	}

	@Override
	public boolean isOccupied() {
		return true;
	}

	@Override
	protected void positionedEvent(ShortPoint2D pos) {
	}

	@Override
	protected int subTimerEvent() {
		return -1;
	}

	@Override
	protected int constructionFinishedEvent() {
		return -1;
	}

	@Override
	protected EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_DOOR;
	}

}
