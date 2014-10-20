package jsettlers.logic.objects.tree;

import jsettlers.common.position.ShortPoint2D;

/**
 * This is a tree on the map, that's adult from the beginning.
 * 
 * @author Andreas Eberle
 * 
 */
public final class AdultTree extends Tree {
	private static final long serialVersionUID = 5956923025331740093L;

	/**
	 * Creates a new adult Tree.
	 * 
	 * @param grid
	 */
	public AdultTree(ShortPoint2D pos) {
		super(pos);
		super.changeState();
	}

	@Override
	protected float getGrowthDuration() {
		return 0.01f;
	}

}
