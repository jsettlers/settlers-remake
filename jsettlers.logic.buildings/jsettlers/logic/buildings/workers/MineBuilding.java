package jsettlers.logic.buildings.workers;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.logic.player.Player;

/**
 * This is a mine building. It's only difference to a {@link WorkerBuilding} is that it's ground won't be flattened.
 * 
 * @author Andreas Eberle
 * 
 */
public final class MineBuilding extends WorkerBuilding {
	private static final long serialVersionUID = 9201058266194063092L;

	public MineBuilding(EBuildingType type, Player player) {
		super(type, player);
	}

	@Override
	protected boolean shouldBeFlatened() {
		return false;
	}
}
