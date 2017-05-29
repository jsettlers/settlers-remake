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
package jsettlers.common.position;

import java.io.Serializable;

public class ShortPoint2D implements Serializable {
	private static final long serialVersionUID = -6227987796843655750L;

	public final short x;
	public final short y;

	public ShortPoint2D(short x, short y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * NOTE: the values of the parameters will be casted to (short). This constructor is just to save typing!
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 */
	public ShortPoint2D(int x, int y) {
		this((short) x, (short) y);
	}

	@Override
	public String toString() {
		return "(" + x + "|" + y + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof ShortPoint2D)) {
			return false;
		}

		return equals((ShortPoint2D) o);
	}

	public boolean equals(ShortPoint2D other) {
		return other != null && other.x == x && other.y == y;
	}

	@Override
	public int hashCode() {
		return hashCode(x, y);
	}

	/**
	 * Computes the hashcode the way ShortPoint2D wants it.
	 * 
	 * @param x
	 *            X coordinate of the position.
	 * @param y
	 *            Y coordinate of the position.
	 * @return Hashcode of the given position.
	 */
	public static int hashCode(int x, int y) {
		return x * 15494071 + y * 12553;
	}

	/**
	 * Gets the number of tiles a movable must at least walk to get from this to the other position.
	 * 
	 * @param otherPos
	 *            The other position.
	 * 
	 * @return The distance a movable needs to walk to get from this to the other position.
	 */
	public int getOnGridDistTo(ShortPoint2D otherPos) {
		final int dx = x - otherPos.x;
		final int dy = y - otherPos.y;
		return getOnGridDist(dx, dy);
	}

	/**
	 * Gets the number of tiles a movable must at least walk to get from this to the other position.
	 *
	 * @param x
	 *            x coordinate of the other position.
	 * @param y
	 *            y coordinate of the other position.
	 * 
	 * @return The distance a movable needs to walk to get from this to the other position.
	 */
	public int getOnGridDistTo(int x, int y) {
		return getOnGridDist(this.x - x, this.y - y);
	}

	/**
	 * Gets the number of tiles a movable must at least walk to get from (0|0) to (dx|dy).
	 * 
	 * @param dx
	 * @param dy
	 * @return
	 */
	public static int getOnGridDist(final int dx, final int dy) {
		final int absDx = Math.abs(dx);
		final int absDy = Math.abs(dy);

		if (dx * dy > 0) { // dx and dy go in the same direction
			if (absDx > absDy) {
				return absDx;
			} else {
				return absDy;
			}
		} else {
			return absDx + absDy;
		}
	}

	public static int getOnGridDist(int startX, int startY, int endX, int endY) {
		return getOnGridDist(endX - startX, endY - startY);
	}

	public boolean equals(int x, int y) {
		return this.x == x && this.y == y;
	}
}
