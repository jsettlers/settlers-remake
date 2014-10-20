package jsettlers.logic.map.random.instructions;

import jsettlers.common.position.ShortPoint2D;

/**
 * This class filters land.
 * 
 * @author michael
 *
 */
public interface LandFilter {
	boolean isPlaceable(ShortPoint2D point);
}
