package jsettlers.common.map.shapes;

import java.io.Serializable;
import java.util.Iterator;

import jsettlers.common.position.ShortPoint2D;

/**
 * allows to iterate over a set of points without the need to create a {@link ShortPoint2D} object.
 * 
 * @author Andreas Eberle
 * 
 */
public interface ICoordinateIterator extends Iterator<ShortPoint2D>, Serializable {

	short getNextX();

	short getNextY();
}
