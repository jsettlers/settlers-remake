package jsettlers.common.position;

import java.io.Serializable;

/**
 * Interface for any class that can be identified with a location in a 2D grid.<br>
 * The x and y coordinates are of type short.
 * 
 * @author Andreas Eberle
 * 
 */
public interface ISPosition2D extends Serializable {
	/**
	 * @return x coordinate of this object.
	 */
	short getX();

	/**
	 * @return y coordinate of this object.
	 */
	short getY();

	/**
	 * 
	 * @param other
	 *            other ISPosition2D object
	 * @return true if two ISPosition2D objects equal each other.<br>
	 *         false otherwise
	 */
	boolean equals(ISPosition2D other);

	/**
	 * Gets the hashcode for this ISPosition.
	 * <p>
	 * The hascode must be computed by <code>x * 15494071 + y * 12553</code>
	 * 
	 * @see ShortPoint2D#hashCode(int, int)
	 */
	@Override
	public int hashCode();
}
