package jsettlers.common.map.shapes;

import java.io.Serializable;
import java.util.Iterator;

import jsettlers.common.position.ISPosition2D;

/**
 * allows to iterate over a set of points without the need to create a {@link ISPosition2D} object.
 * 
 * @author Andreas Eberle
 * 
 */
public interface ICoordinateIterator extends Iterator<ISPosition2D>, Serializable {

	short getNextX();

	short getNextY();
}
