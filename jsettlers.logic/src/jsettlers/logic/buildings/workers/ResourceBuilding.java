package jsettlers.logic.buildings.workers;

import jsettlers.algorithms.datastructures.BooleanMovingAverage;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.logic.player.Player;

/**
 * A {@link WorkerBuilding} implementing the {@link IResourceBuilding} interface to supply productivity information to the UI.
 * 
 * @author Andreas Eberle
 *
 */
public class ResourceBuilding extends WorkerBuilding implements IBuilding.IResourceBuilding {
	private static final long serialVersionUID = -828476867565686860L;

	private final BooleanMovingAverage movingAverage;

	public ResourceBuilding(EBuildingType type, Player player, int movingAverageElements) {
		super(type, player);
		movingAverage = new BooleanMovingAverage(movingAverageElements, false);
	}

	@Override
	public float getProductivity() {
		return movingAverage.getAverage();
	}

	protected void productivityActionExecuted(boolean successfully) {
		movingAverage.inserValue(successfully);
	}
}
