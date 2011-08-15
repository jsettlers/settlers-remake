package jsettlers.logic.objects.corn;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.objects.GrowingObject;
import jsettlers.logic.objects.IMapObjectRemovableGrid;

/**
 * This is a Corn on the map.
 * 
 * @author Andreas Eberle
 * 
 */
public class Corn extends GrowingObject {

	/**
	 * Creates a new Corn.
	 * 
	 * @param grid
	 */
	public Corn(IMapObjectRemovableGrid grid) {
		super(grid, EMapObjectType.CORN_GROWING, EMapObjectType.CORN_ADULT, EMapObjectType.CORN_DEAD);
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

	@Override
	protected float getGrowthIncrease() {
		return Constants.CORN_GROWTH_PER_INTERRUPT;
	}

}
