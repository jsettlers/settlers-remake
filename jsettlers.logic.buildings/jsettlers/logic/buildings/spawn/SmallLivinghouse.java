package jsettlers.logic.buildings.spawn;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.movable.EMovableType;

public class SmallLivinghouse extends SpawnBuilding {
	static final int PRODUCE_LIMIT = 10;

	public SmallLivinghouse(byte player) {
		super(EBuildingType.SMALL_LIVINGHOUSE, player);
	}

	@Override
	protected EMovableType getMovableType() {
		return EMovableType.BEARER;
	}

	@Override
	protected int getProduceLimit() {
		return PRODUCE_LIMIT;
	}
}
