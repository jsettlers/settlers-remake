package jsettlers.logic.objects.tree;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.RelativePoint;
import jsettlers.logic.objects.GrowingObject;

/**
 * This is a tree on the map.
 * 
 * @author Andreas Eberle
 * 
 */
public class Tree extends GrowingObject {
	private static final long serialVersionUID = 8241068714975746824L;

	/**
	 * time a tree needs to grow
	 */
	public static final float GROWTH_DURATION = 7 * 60;
	public static final float DECOMPOSE_DURATION = 2 * 60;

	private static final RelativePoint[] BLOCKED = new RelativePoint[] { new RelativePoint(0, 0) };

	/**
	 * Creates a new Tree.
	 * 
	 * @param grid
	 */
	public Tree(ISPosition2D pos) {
		super(pos, EMapObjectType.TREE_GROWING, EMapObjectType.TREE_ADULT, EMapObjectType.TREE_DEAD);
	}

	@Override
	public RelativePoint[] getBlockedTiles() {
		return BLOCKED;
	}

	@Override
	protected float getGrowthDuration() {
		return GROWTH_DURATION;
	}

	@Override
	protected float getDecomposeDuration() {
		return DECOMPOSE_DURATION;
	}
}
