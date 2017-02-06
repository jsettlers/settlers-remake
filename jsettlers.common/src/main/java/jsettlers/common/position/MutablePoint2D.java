/*******************************************************************************
 * Copyright (c) 2015 - 2017
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.common.position;

/**
 * Created by Andreas Eberle
 */
public class MutablePoint2D {
	public int x;
	public int y;

	public MutablePoint2D() {
	}

	public MutablePoint2D(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "MutablePoint2D{" +
				"x=" + x +
				", y=" + y +
				'}';
	}

	public ShortPoint2D toShortPoint2D() {
		return new ShortPoint2D(x, y);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MutablePoint2D)) return false;

		MutablePoint2D that = (MutablePoint2D) o;

		if (x != that.x) return false;
		return y == that.y;

	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		return result;
	}

	public ShortPoint2D createShortPoint2D() {
		return new ShortPoint2D(x, y);
	}
}
