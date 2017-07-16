/*******************************************************************************
 * Copyright (c) 2017
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
package jsettlers.common.utils.coordinates;

import jsettlers.common.position.ShortPoint2D;

import java.util.Arrays;

/**
 * 
 * @author Andreas Eberle
 */
public class CoordinatesList extends CoordinateStream {
	private static final int SHORT_MASK = 0x7fff;

	private static final int STANDARD_INITIAL_CAPACITY = 16;
	public static final int STREAM_INITIALIZATION_INITIAL_CAPACITY = 128;

	private int[] points;
	private int size = 0;

	public CoordinatesList() {
		this(STANDARD_INITIAL_CAPACITY);
	}

	public CoordinatesList(int initialCapacity) {
		points = new int[initialCapacity];
	}

	public CoordinatesList(CoordinateStream coordinateStream) {
		this(STREAM_INITIALIZATION_INITIAL_CAPACITY);
		coordinateStream.forEach(this::add);
	}

	public void add(int x, int y) {
		int pos = pack(x, y);
		if (points.length == size) {
			resizeTo(points.length * 2);
		}
		points[size] = pos;
		size++;
	}

	private void resizeTo(int arraySize) {
		points = Arrays.copyOf(points, arraySize);
	}

	public void clear() {
		size = 0;
		points = new int[STANDARD_INITIAL_CAPACITY];
	}

	public ShortPoint2D get(int index) {
		return new ShortPoint2D(unpackX(points[index]), unpackY(points[index]));
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size <= 0;
	}

	@Override
	public boolean iterate(IBooleanCoordinateFunction function) {
		for (int i = 0; i < size; i++) {
			int packedCoordinate = points[i];
			int x = unpackX(packedCoordinate);
			int y = unpackY(packedCoordinate);

			if (!function.apply(x, y)) {
				return false;
			}
		}
		return true;
	}

	private static int pack(int x, int y) {
		return ((x & SHORT_MASK) << 16) | (y & SHORT_MASK);
	}

	private static int unpackX(int pos) {
		return pos >> 16;
	}

	private static int unpackY(int pos) {
		return pos & SHORT_MASK;
	}
}
