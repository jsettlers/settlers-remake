package jsettlers.common.mapobject;

import jsettlers.common.movable.EDirection;

/**
 * Specifies a arrow that comes from {@link #getSource()} and flies to {@link #getTarget()}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IArrowMapObject extends IMapObject {

	/**
	 * gets the x coordinate of the source position of the arrow.
	 * 
	 * @return The start x coordinate of the position where it was shot from
	 */
	short getSourceX();

	/**
	 * gets the x coordinate of the source position of the arrow.
	 * 
	 * @return The start x coordinate of the position where it was shot from
	 */
	short getSourceY();

	/**
	 * gets the x coordinate of the target position of the arrow.
	 * 
	 * @return the target x coordinate
	 */
	short getTargetX();

	/**
	 * gets the x coordinate of the target position of the arrow.
	 * 
	 * @return the target x coordinate
	 */
	short getTargetY();

	/**
	 * gets the direction in which the arrow is flying.
	 * 
	 * @return The direciton
	 */
	EDirection getDirection();
}
