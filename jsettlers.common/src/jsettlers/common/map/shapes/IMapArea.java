package jsettlers.common.map.shapes;

import java.io.Serializable;
import java.util.Iterator;

import jsettlers.common.position.ShortPoint2D;

/**
 * This defines an area on the map of a given shape.
 * 
 * TODO: Not all map Areas are serializable
 * 
 * @author michael
 */
public interface IMapArea extends Iterable<ShortPoint2D>, Serializable {
	/**
	 * Checks whether the given position is contained by the shape.
	 * <p>
	 * It is not guaranteed that they are also on the map.
	 * 
	 * @param position
	 *            The position.
	 */
	boolean contains(ShortPoint2D position);

	/**
	 * Gets an iterator for the shape that returns all tiles that are contained by this shape.
	 * <p>
	 * The iterator iterates over all positions for which {@link #contains(ShortPoint2D)} returns true and returns each position exactly one.
	 * 
	 * @return An Iterator over the area in the shape.
	 */
	@Override
	public Iterator<ShortPoint2D> iterator();
}
