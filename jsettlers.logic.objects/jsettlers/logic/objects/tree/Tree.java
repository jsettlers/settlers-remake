package jsettlers.logic.objects.tree;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.RelativePoint;
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

	private static final RelativePoint[] BLOCKED = new RelativePoint[] {
    		new RelativePoint(0, 0)
    };

	/**
	 * Creates a new Tree.
	 * 
	 * @param grid
	 */
	public Tree(IMapObjectRemovableGrid grid, ISPosition2D pos) {
		super(grid, pos, EMapObjectType.TREE_GROWING, EMapObjectType.TREE_ADULT, EMapObjectType.TREE_DEAD);
	}

	@Override
	public RelativePoint[] getBlockedTiles() {
	    return BLOCKED;
	}

	@Override
	protected float getGrowthIncrease() {
		return Constants.TREE_GROWTH_PER_INTERRUPT;
	}

}
