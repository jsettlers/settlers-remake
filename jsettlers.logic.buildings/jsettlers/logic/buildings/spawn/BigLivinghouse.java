package jsettlers.logic.buildings.spawn;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.movable.EMovableType;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class BigLivinghouse extends SpawnBuilding {
	private static final long serialVersionUID = -6442369297239688436L;

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
