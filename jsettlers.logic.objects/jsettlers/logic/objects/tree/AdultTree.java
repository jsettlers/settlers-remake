package jsettlers.logic.objects.tree;

import jsettlers.common.position.ISPosition2D;

/**
 * This is a tree on the map, that's adult from the beginning.
 * 
 * @author Andreas Eberle
 * 
 */
public class AdultTree extends Tree {

	/**
	 * Creates a new adult Tree.
	 * 
	 * @param grid
	 */
	public AdultTree(ISPosition2D pos) {
		super(pos);
		super.changeState();
	}

	@Override
	protected float getGrowthDuration() {
		return 0;
	}

}
