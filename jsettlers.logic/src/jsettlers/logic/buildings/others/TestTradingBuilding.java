package jsettlers.logic.buildings.others;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.player.Player;

public class TestTradingBuilding extends Building implements IBuilding.ITrading {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1760409147232184087L;
	private boolean isSeaTrading;

	public TestTradingBuilding(EBuildingType type, Player player, boolean isSeaTrading) {
		super(type, player);
		this.isSeaTrading = isSeaTrading;
	}

	@Override
	public boolean isOccupied() {
		return false;
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

	@Override
	public int getRequestedTradingFor(EMaterialType material) {
		return 0;
	}

	@Override
	public boolean isSeaTrading() {
		return isSeaTrading;
	}

}
