package jsettlers.logic.buildings;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ISPosition2D;

public class TestBuilding extends Building {

	protected TestBuilding(byte player, EBuildingType type) {
		super(type, player);
	}

	@Override
	public boolean isOccupied() {
		return false;
	}

	@Override
	public void stopOrStartWorking(boolean stop) {
	}

	@Override
	protected void constructionFinishedEvent() {
	}

	@Override
	protected void subTimerEvent() {
	}

	@Override
	protected EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_DOOR;
	}

	@Override
	protected void positionedEvent(ISPosition2D pos) {
	}

}
