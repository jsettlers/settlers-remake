package jsettlers.logic.objects.tree;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.objects.GrowingObject;
import jsettlers.logic.objects.IMapObjectRemovableGrid;

/**
 * This is a tree on the map.
 * 
 * @author Andreas Eberle
 * 
 */
public class Tree extends GrowingObject {

	/**
	 * Creates a new Tree.
	 * 
	 * @param grid
	 */
	public Tree(IMapObjectRemovableGrid grid) {
		super(grid, EMapObjectType.TREE_GROWING, EMapObjectType.TREE_ADULT, EMapObjectType.TREE_DEAD);
	}

	@Override
	public boolean isBlocking() {
		return true;
	}

	@Override
	protected float getGrowthIncrease() {
		return Constants.TREE_GROWTH_PER_INTERRUPT;
	}

}
