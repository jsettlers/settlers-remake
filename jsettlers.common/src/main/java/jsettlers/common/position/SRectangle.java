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

public class SRectangle implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 3854066932718449211L;

	public final short xMin;
	public final short yMin;
	public final short xMax;
	public final short yMax;

	public SRectangle(short xMin, short yMin, short xMax, short yMax) {
		this.xMin = xMin;
		this.yMin = yMin;
		this.xMax = xMax;
		this.yMax = yMax;
	}

	public int getWidth() {
		return xMax - xMin + 1;
	}

	public int getHeight() {
		return yMax - yMin + 1;
	}

	public boolean contains(ShortPoint2D pos) {
		return xMin <= pos.x && pos.x <= xMax && yMin <= pos.y && pos.y <= yMax;
	}

	@Override
	public String toString() {
		return "xMin: " + xMin + " yMin: " + yMin + "  xMax: " + xMax + " yMax: " + yMax;
	}
}
