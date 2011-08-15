package jsettlers.common.mapobject;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ISPosition2D;

/**
 * Specifies a arrow that comes from {@link #getSource()} and flies to {@link #getTarget()}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IArrowMapObject extends IMapObject {

	/**
	 * gets the source position of the arrow.
	 * 
	 * @return The start position where it was shot from
	 */
	ISPosition2D getSource();

	/**
	 * gets the target position of the arrow.
	 * 
	 * @return The target
	 */
	ISPosition2D getTarget();

	/**
	 * gets the direction in which the arrow is flying.
	 * 
	 * @return The direciton
	 */
	EDirection getDirection();
}
