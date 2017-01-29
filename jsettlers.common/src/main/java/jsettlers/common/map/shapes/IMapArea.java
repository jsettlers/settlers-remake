/*******************************************************************************
 * Copyright (c) 2015 - 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.common.map.shapes;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.coordinates.CoordinateStream;

import java.io.Serializable;
import java.util.Iterator;

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

	boolean contains(int x, int y);

	/**
	 * Gets an iterator for the shape that returns all tiles that are contained by this shape.
	 * <p>
	 * The iterator iterates over all positions for which {@link #contains(ShortPoint2D)} returns true and returns each position exactly one.
	 * 
	 * @return An Iterator over the area in the shape.
	 */
	@Override
	Iterator<ShortPoint2D> iterator();

	CoordinateStream stream();
}
