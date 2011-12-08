package jsettlers.logic.buildings;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ISPosition2D;

public class TestBuilding extends Building {
	private static final long serialVersionUID = -1043442154922289693L;

	protected TestBuilding(byte player, EBuildingType type) {
		super(type, player);
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
