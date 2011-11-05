package jsettlers.logic.buildings.spawn;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.movable.EMovableType;

public class MediumLivinghouse extends SpawnBuilding {
	static final int PRODUCE_LIMIT = 30;

	public MediumLivinghouse(byte player) {
		super(EBuildingType.MEDIUM_LIVINGHOUSE, player);
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
