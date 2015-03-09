package jsettlers.logic.buildings.spawn;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.player.Player;

/**
 * This class represents a big apartment house that spawns {@link #PRODUCE_LIMIT} ({@value #PRODUCE_LIMIT}) new bearers.
 * 
 * 
 * @author Andreas Eberle
 * 
 */
public final class SmallLivinghouse extends SpawnBuilding {
	private static final long serialVersionUID = -6001054088627024255L;

	private static final byte PRODUCE_LIMIT = 10;

	public SmallLivinghouse(Player player) {
		super(EBuildingType.SMALL_LIVINGHOUSE, player);
	}

	@Override
	protected EMovableType getMovableType() {
		return EMovableType.BEARER;
	}

	@Override
	protected byte getProduceLimit() {
		return PRODUCE_LIMIT;
	}
}
