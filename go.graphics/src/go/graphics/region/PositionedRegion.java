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
package go.graphics.region;

import go.graphics.UIPoint;

/**
 * This is the position of a region inside an area.
 * 
 * @author michael
 */
public class PositionedRegion {
	private final int top;
	private final int bottom;
	private final int left;
	private final int right;
	private final Region region;

	public PositionedRegion(Region region, int top, int bottom, int left,
			int right) {
		this.region = region;
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;

	}

	public int getTop() {
		return top;
	}

	public int getBottom() {
		return bottom;
	}

	public int getLeft() {
		return left;
	}

	public int getRight() {
		return right;
	}

	public Region getRegion() {
		return region;
	}

	public boolean contentContains(UIPoint point) {
		return getLeft() <= point.getX() && getRight() > point.getX()
				&& getTop() > point.getY() && getBottom() <= point.getY();
	}
}
