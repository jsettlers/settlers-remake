package jsettlers.logic.buildings.spawn;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.movable.EMovableType;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class MediumLivinghouse extends SpawnBuilding {
	private static final long serialVersionUID = 6182479871695461138L;

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
