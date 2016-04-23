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
package jsettlers.mapcreator.tools.buffers;

public class GlobalShapeBuffer {
	private final int width;
	private final int height;
	private final byte[][] data;

	public GlobalShapeBuffer(int width, int height) {
		this.width = width;
		this.height = height;
		this.data = new byte[width][height];
	}

	public byte[][] getArray(int usedminx, int usedminy, int usedmaxx,
			int usedmaxy) {
		if (usedminy < 0) {
			usedminy = 0;
		}
		if (usedmaxy >= height) {
			usedmaxy = height - 1;
		}
		if (usedminx < 0) {
			usedminx = 0;
		}
		if (usedmaxx >= width) {
			usedmaxx = width - 1;
		}

		for (int y = usedminy; y < usedmaxy; y++) {
			for (int x = usedminx; x < usedmaxx; x++) {
				data[x][y] = 0;
			}
		}
		return data;
	}

	public byte[][] getArray(int x, int y, int radius) {
		return getArray(x - radius, y - radius, x + radius, y + radius);
	}
}
