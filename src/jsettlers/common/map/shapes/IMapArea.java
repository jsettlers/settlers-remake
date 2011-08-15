package jsettlers.common.map.shapes;

import java.util.Iterator;

import jsettlers.common.position.ISPosition2D;

/**
 * This defines an area on the map of a given shape.
 * 
 * @author michael
 */
public interface IMapArea extends Iterable<ISPosition2D> {
	/**
	 * Checks whether the given position is contained by the shape.
	 * <p>
	 * It is not guaranteed that they are also on the map.
	 * 
	 * @param position
	 *            The position.
	 */
	boolean contains(ISPosition2D position);

	/**
	 * Gets an iterator for the shape that returns all tiles that are contained
	 * by this shape.
	 * <p>
	 * The iterator iterates over all positions for which
	 * {@link #contains(ISPosition2D)} returns true and returns each position
	 * exactly one.
	 * 
	 * @return An Iterator over the area in the shape.
	 */
	public Iterator<ISPosition2D> iterator();
}
