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

	/**
	 * How many materials were requested by the user. Integer#MAX_VALUE for infinity.
	 */
	private final int[] requestedMaterials = new int[EMaterialType.NUMBER_OF_MATERIALS];

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
		return requestedMaterials[material.ordinal];
	}

	@Override
	public boolean isSeaTrading() {
		return isSeaTrading;
	}

	public void changeRequestedMaterial(EMaterialType material, int amount, boolean relative) {
		long newValue = amount;
		if (relative) {
			int old = requestedMaterials[material.ordinal];
			if (old == Integer.MAX_VALUE) {
				// infinity stays infinity.
				return;
			}
			newValue += old;
		}

		requestedMaterials[material.ordinal] = (int) Math.max(0, Math.min(Integer.MAX_VALUE, newValue));
	}

}
