package jsettlers.logic.map.random.instructions;

import jsettlers.common.position.ISPosition2D;

/**
 * This class filters land.
 * 
 * @author michael
 *
 */
public interface LandFilter {
	boolean isPlaceable(ISPosition2D point);
}
