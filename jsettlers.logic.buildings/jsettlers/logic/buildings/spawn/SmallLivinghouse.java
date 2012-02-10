package jsettlers.logic.buildings.spawn;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.movable.EMovableType;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public final class SmallLivinghouse extends SpawnBuilding {
	private static final long serialVersionUID = -6001054088627024255L;

	private static final byte PRODUCE_LIMIT = 10;

	public SmallLivinghouse(byte player) {
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
