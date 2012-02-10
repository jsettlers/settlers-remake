package jsettlers.logic.objects.tree;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.sound.ISoundable;
import jsettlers.logic.objects.GrowingObject;

/**
 * This is a tree on the map.
 * 
 * @author Andreas Eberle
 * 
 */
public class Tree extends GrowingObject implements ISoundable {
	private static final long serialVersionUID = 8241068714975746824L;

	public static final float GROWTH_DURATION = 7 * 60;
	public static final float DECOMPOSE_DURATION = 2 * 60;

	private static final RelativePoint[] BLOCKED = new RelativePoint[] { new RelativePoint(0, 0) };

	private boolean soundPlayed;

	/**
	 * Creates a new Tree.
	 * 
	 * @param grid
	 */
	public Tree(ISPosition2D pos) {
		super(pos, EMapObjectType.TREE_GROWING);
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

	@Override
	public void setSoundPlayed() {
		soundPlayed = true;
	}

	@Override
	public boolean isSoundPlayed() {
		return soundPlayed;
	}

	@Override
	protected EMapObjectType getDeadState() {
		return EMapObjectType.TREE_DEAD;
	}

	@Override
	protected EMapObjectType getAdultState() {
		return EMapObjectType.TREE_ADULT;
	}
}
