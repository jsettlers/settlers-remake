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
package jsettlers.mapcreator.tools.shapes;

import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.MathUtils;

/**
 * This is a line that ends in two circles.
 * 
 * @author michael
 */
public class CircleLine {

	private final short startx;
	private final short starty;
	private final short endx;
	private final short endy;
	private final double directionx;
	private final double directiony;
	private final double length;

	public CircleLine(ShortPoint2D start, ShortPoint2D end) {
		this.startx = start.x;
		this.starty = start.y;
		this.endx = end.x;
		this.endy = end.y;

		int nx = endx - startx;
		int ny = endy - starty;
		this.length = MathUtils.hypot(nx, ny);
		// vector pointing in the direction of the line
		this.directionx = nx / length;
		this.directiony = ny / length;
	}

	/**
	 * Gets the distance to the center Line.
	 * 
	 * @param x
	 *            The x position
	 * @param y
	 *            THe y position.
	 */
	public double getDistanceOf(int x, int y) {
		int dx = x - startx;
		int dy = y - starty;

		double t = directionx * dx + directiony * dy;
		if (t < 0) {
			// check distance to start circle
			return MapCircle.getDistance(x, y, startx, starty);
		} else if (t > length) {
			// after end circle
			return MapCircle.getDistance(x, y, endx, endy);
		} else {
			int cx = startx + (int) (directionx * t);
			int cy = starty + (int) (directiony * t);
			return MapCircle.getDistance(x, y, cx, cy);
		}
	}
}
