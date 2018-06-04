/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
package jsettlers.mapcreator.tools.shapes;

import jsettlers.common.position.ShortPoint2D;

/**
 * Only draws a little point at the start position.
 *
 * @author michael
 *
 */
public class PointShape extends ShapeType {

	/**
	 * Constructor
	 */
	public PointShape() {
		super("point");
	}

	@Override
	public void setAffectedStatus(byte[][] fields, ShortPoint2D start, ShortPoint2D end) {
		short x = start.x;
		if (x >= 0 && x < fields.length) {
			short y = start.y;
			if (y >= 0 && y < fields[x].length) {
				fields[x][y] = Byte.MAX_VALUE;
			}
		}
	}

	@Override
	public int getSize() {
		return 1;
	}
}
