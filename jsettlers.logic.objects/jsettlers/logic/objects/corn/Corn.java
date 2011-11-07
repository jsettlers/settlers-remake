package jsettlers.logic.objects.corn;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.objects.GrowingObject;

/**
 * This is a Corn on the map.
 * 
 * @author Andreas Eberle
 * 
 */
public class Corn extends GrowingObject {
	private static final long serialVersionUID = -7535441306083940418L;

	public static final float GROWTH_DURATION = 3 * 60;
	public static final float DECOMPOSE_DURATION = 2 * 60;
	public static final float REMOVE_DURATION = 1 * 60;

	/**
	 * Creates a new Corn.
	 * 
	 * @param grid
	 */
	public Corn(ISPosition2D pos) {
		super(pos, EMapObjectType.CORN_GROWING, EMapObjectType.CORN_ADULT, EMapObjectType.CORN_DEAD);
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
