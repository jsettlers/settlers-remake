package jsettlers.logic.buildings.spawn;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.movable.EMovableType;

public class BigLivinghouse extends SpawnBuilding {
	static final int PRODUCE_LIMIT = 100;

	public BigLivinghouse(byte player) {
		super(EBuildingType.BIG_LIVINGHOUSE, player);
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
