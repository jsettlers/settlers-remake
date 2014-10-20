package jsettlers.logic.buildings.spawn;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.player.Player;

/**
 * This class represents a medium apartment house that spawns {@link #PRODUCE_LIMIT} ({@value #PRODUCE_LIMIT}) new bearers.
 * 
 * @author Andreas Eberle
 * 
 */
public final class MediumLivinghouse extends SpawnBuilding {
	private static final long serialVersionUID = 6182479871695461138L;

	private static final byte PRODUCE_LIMIT = 30;

	public MediumLivinghouse(Player player) {
		super(EBuildingType.MEDIUM_LIVINGHOUSE, player);
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
