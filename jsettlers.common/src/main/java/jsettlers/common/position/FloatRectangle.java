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
package jsettlers.common.position;

/**
 * This is an int rectangle. It covers the area from (including) x1 to (unincluding) x2.
 * 
 * @author michael
 */
public class FloatRectangle {
	private final float minx, miny, maxx, maxy;

	public FloatRectangle(float minx, float miny, float maxx, float maxy) {
		this.minx = minx;
		this.miny = miny;
		this.maxx = maxx;
		this.maxy = maxy;
	}

	/**
	 * Gets the minimal x coordinate for a point that is inside the rect.
	 * 
	 * @return The x coordinate.
	 */
	public float getMinX() {
		return minx;
	}

	/**
	 * Gets the minimal y coordinate for a point that is inside the rect.
	 * 
	 * @return The y coordinate.
	 */
	public float getMinY() {
		return miny;
	}

	/**
	 * Gets the x coordinate for a point that is just outside of rect. Points with a x coordinate smaller than this value are inside the rect.
	 * 
	 * @return The x coordinate.
	 */
	public float getMaxX() {
		return maxx;
	}

	/**
	 * Gets the x coordinate for a point that is just outside of rect. Points with a x coordinate smaller than this value are inside the rect.
	 * 
	 * @return The x coordinate.
	 */
	public float getMaxY() {
		return maxy;
	}

	/**
	 * Gets the width of the rectangle. That states how many pixels are contained in it in x direction.
	 * 
	 * @return The width
	 */
	public float getWidth() {
		return maxx - minx;
	}

	/**
	 * Gets the height of the rectangle. That states how many pixels are contained in it in y direction.
	 * 
	 * @return The height
	 */
	public float getHeight() {
		return maxy - miny;
	}

	/**
	 * Gets the center of the rectangle in x direction.
	 * 
	 * @return The center.
	 */
	public float getCenterX() {
		return (minx + maxx) / 2;
	}

	/**
	 * Gets the center of the rectangle in y direction.
	 * 
	 * @return The center.
	 */
	public float getCenterY() {
		return (miny + maxy) / 2;
	}

	/**
	 * Checks whether a point is inside the rectangle.
	 * 
	 * @param x
	 *            The x coordinate to check
	 * @param y
	 *            The y coordinate to check
	 * @return If the point is inside.
	 */
	public boolean contains(float x, float y) {
		return x >= minx && x < maxx && y >= miny && y < maxy;
	}

	/**
	 * Shrinks the rectangle by the given amount. The center of the new rectangle is the same as the one of the old one, but its size is in each
	 * direction 2*border bigger.
	 * 
	 * @param border
	 *            the size to reduce the rectangle on each side. If it is negative, the rectangle is made smaller.
	 * @return The bigger rectangle.
	 */
	public FloatRectangle bigger(float border) {
		return new FloatRectangle(minx - border, miny - border, maxx + border,
				maxy + border);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FloatRectangle) {
			return equals((FloatRectangle) obj);
		} else {
			return false;
		}
	}

	private boolean equals(FloatRectangle rect) {
		return rect.minx == minx && rect.miny == miny && rect.maxx == maxx
				&& rect.maxy == maxy;
	}

	@Override
	public String toString() {
		return "rect[minx=" + minx + ",miny=" + miny + ",maxx=" + maxx
				+ ",maxy=" + maxy + "]";
	}

	@Override
	public int hashCode() {
		return Float.floatToIntBits(minx) * 104729 + Float.floatToIntBits(miny)
				* 4900099 + Float.floatToIntBits(maxx) * 135084239
				+ Float.floatToIntBits(maxy);
	}
}