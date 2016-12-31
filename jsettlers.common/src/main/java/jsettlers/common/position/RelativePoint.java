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

import java.io.Serializable;

/**
 * class to specify a relative position on the grid
 * 
 * @author Andreas Eberle
 * 
 */
public class RelativePoint implements Serializable {
	private static final long serialVersionUID = 7216627427441018520L;
	private final short dy;
	private final short dx;

	public RelativePoint(short dx, short dy) {
		this.dy = dy;
		this.dx = dx;
	}

	/**
	 * constructor for easy use (ints instead of shorts)<br>
	 * NOTE: all arguments will be cast to short !!
	 * 
	 * @param dx
	 * @param dy
	 */
	public RelativePoint(int dx, int dy) {
		this((short) dx, (short) dy);
	}

	/**
	 * calculates the point on the grid from the given start point.
	 * 
	 * @param start
	 * @return
	 */
	public final ShortPoint2D calculatePoint(ShortPoint2D start) {
		return calculatePoint(start.x, start.y);
	}

	/**
	 * calculates the point on the grid from the given start coordinates.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public final ShortPoint2D calculatePoint(int x, int y) {
		return new ShortPoint2D(x + dx, y + dy);
	}

	public final int calculateX(int x) {
		return x + dx;
	}

	public final int calculateY(int y) {
		return y + dy;
	}

	public static final RelativePoint getRelativePoint(ShortPoint2D start, ShortPoint2D end) {
		short dx = (short) (end.x - start.x);
		short dy = (short) (end.y - start.y);

		return new RelativePoint(dx, dy);
	}

	@Override
	public final boolean equals(Object o) {
		if (o != null && (o instanceof RelativePoint)) {
			RelativePoint other = (RelativePoint) o;
			return other.getDy() == this.getDy() && other.getDx() == this.getDx();
		} else {
			return false;
		}
	}

	@Override
	public final int hashCode() {
		return (getDy() << 16) + getDx();
	}

	@Override
	public final String toString() {
		return "dx=" + getDx() + ", dy=" + getDy();
	}

	public final short getDy() {
		return dy;
	}

	public final short getDx() {
		return dx;
	}

	public RelativePoint invert() {
		return new RelativePoint(-dx, -dy);
	}

}