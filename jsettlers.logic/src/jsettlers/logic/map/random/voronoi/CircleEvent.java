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
package jsettlers.logic.map.random.voronoi;

import jsettlers.logic.map.random.geometry.Point2D;

public class CircleEvent implements VoronoiEvent {
	private final BeachLinePart bottom;
	private final BeachLinePart middle;
	private final BeachLinePart top;
	private double radius;
	private Point2D center;

	public CircleEvent(BeachLinePart bottom, BeachLinePart middle,
			BeachLinePart top) {
		assert bottom.getY() <= middle.getY() && middle.getY() <= top.getY();
		this.bottom = bottom;
		this.middle = middle;
		this.top = top;

	}

	public Point2D getCenter() {
		if (center == null) {
			double ax = getBottom().getX();
			double ay = getBottom().getY();
			double bx = getMiddle().getX();
			double by = getMiddle().getY();
			double cx = getTop().getX();
			double cy = getTop().getY();
			double d = 2 * (ax + (by - cy) + bx * (cy - ay) + cx * (ay - by));
			double x =
					((ay * ay + ax * ax) * (by - cy) + (by * by + bx * bx)
							* (cy - ay) + (cy * cy + cx * cx) * (ay - by))
							/ d;
			double y =
					((ay * ay + ax * ax) * (cx - by) + (by * by + bx * bx)
							* (ax - cx) + (cy * cy + cx * cx) * (bx - ax))
							/ d;
			this.radius = Math.sqrt((x - ax) * (x - ax) + (y - ay) * (y - ay));
			this.center = new Point2D(x, y);
		}
		return this.center;
	}

	private double getRadius() {
		if (center == null) {
			getCenter();
		}
		return radius;
	}

	@Override
	public double getX() {
		return getCenter().getX() + getRadius();
	}

	@Override
	public boolean isVoronoiSite() {
		return false;
	}

	public BeachLinePart getBottom() {
		return bottom;
	}

	public BeachLinePart getMiddle() {
		return middle;
	}

	public BeachLinePart getTop() {
		return top;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

}
