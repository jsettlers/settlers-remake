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
package jsettlers.logic.map.random.geometry;

public class Point2D {

	private final double y;
	private final double x;

	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point2D interpolate(Point2D to, double percentage) {
		return new Point2D(to.getX() * percentage + getX() * (1 - percentage),
				to.getY() * percentage + getY() * (1 - percentage));
	}

	public double getY() {
		return y;
	}

	public double getX() {
		return x;
	}

	public double distanceSquared(Point2D center) {
		double dx = center.x - x;
		double dy = center.y - y;
		return dx * dx + dy * dy;
	}

	public Point getIntPoint() {
		return new Point((int) Math.round(x), (int) Math.round(y));
	}

	public double getDirectionTo(Point2D point) {
		return Math.atan2(y - point.y, x - point.x);
	}
}
