/*******************************************************************************
 * Copyright (c) 2015
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

import java.util.Iterator;

import jsettlers.common.position.ShortPoint2D;

public class MapCircleBorder implements IMapArea {
	private static final long serialVersionUID = 7850239131055274940L;

	private final MapCircle baseCircle;

	public MapCircleBorder(MapCircle baseCircle) {
		this.baseCircle = baseCircle;
	}

	/**
	 * Tests whether a point is on the border of the circle.
	 * <p>
	 * This is exactly the case if the point is contained in the base circle and it is not contained int he volume.
	 * 
	 * @see IMapArea#contains(ShortPoint2D)
	 * @see MapCircleBorder#isInVolume(ShortPoint2D)
	 */
	@Override
	public boolean contains(ShortPoint2D position) {
		return baseCircle.contains(position) && !isInVolume(position);
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return new MapCircleBorderIterator(this);
	}

	public MapCircle getBaseCircle() {
		return baseCircle;
	}

	/**
	 * Calculates whether a point is in the volume of the base circle and therefore not on the border.
	 * 
	 * @param point
	 *            The point to test
	 * @return true if and only if the point is in the volume.
	 */
	public boolean isInVolume(ShortPoint2D point) {
		if (point == null) {
			return false;
		}
		short line = point.y;
		float prevLineWidth = baseCircle.getHalfLineWidth(line - baseCircle.getCenterY() - 1);
		float nextLineWidth = baseCircle.getHalfLineWidth(line - baseCircle.getCenterY() + 1);
		float xDistToCenter = Math.abs(-point.x - .5f * (baseCircle.getCenterY() - line) + baseCircle.getCenterX());
		return xDistToCenter < prevLineWidth && xDistToCenter < nextLineWidth;
	}

}
