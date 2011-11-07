package jsettlers.logic.buildings.spawn;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.movable.EMovableType;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class SmallLivinghouse extends SpawnBuilding {
	private static final long serialVersionUID = -6001054088627024255L;

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
