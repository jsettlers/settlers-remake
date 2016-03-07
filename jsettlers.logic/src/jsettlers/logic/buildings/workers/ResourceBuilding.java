package jsettlers.logic.buildings.workers;

import jsettlers.algorithms.datastructures.BooleanMovingAverage;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.player.Player;

/**
 * A {@link WorkerBuilding} implementing the {@link IResourceBuilding} interface to supply productivity information to the UI.
 * 
 * @author Andreas Eberle
 *
 */
public class ResourceBuilding extends WorkerBuilding implements IBuilding.IResourceBuilding {
	private static final long serialVersionUID = -828476867565686860L;
	private static final int NUMBER_OF_MOVING_AVERAGE_ELEMENTS = 12;

	private final BooleanMovingAverage movingAverage;

	public ResourceBuilding(EBuildingType type, Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		super(type, player, position, buildingsGrid);
		movingAverage = new BooleanMovingAverage(NUMBER_OF_MOVING_AVERAGE_ELEMENTS, false);
	}

	@Override
	public float getProductivity() {
		return movingAverage.getAverage();
	}

	@Override
	public int getRemainingResourceAmount() {
		return -1;
	}

	protected void productivityActionExecuted(boolean successfully) {
		movingAverage.inserValue(successfully);
	}
}
