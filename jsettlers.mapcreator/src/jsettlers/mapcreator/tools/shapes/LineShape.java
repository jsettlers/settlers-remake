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
package jsettlers.mapcreator.tools.shapes;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;

public class LineShape extends ShapeType {

	/**
	 * Constructor
	 */
	public LineShape() {
		super("line");
	}

	@Override
	public void setAffectedStatus(byte[][] fields, ShortPoint2D start, ShortPoint2D end) {
		ShortPoint2D current = start;
		if (shouldDrawAt(current)) {
			setFieldToMax(fields, current);
		}
		while (!current.equals(end)) {
			EDirection d = EDirection.getApproxDirection(current, end);
			current = d.getNextHexPoint(current);
			if (shouldDrawAt(current)) {
				setFieldToMax(fields, current);
			}
		}
	}

	protected boolean shouldDrawAt(ShortPoint2D current) {
		return true;
	}

	private static void setFieldToMax(byte[][] fields, ShortPoint2D current) {
		short x = current.x;
		short y = current.y;
		if (x < fields.length && x >= 0 && y >= 0 && y < fields[x].length) {
			fields[x][y] = Byte.MAX_VALUE;
		}
	}

	@Override
	public int getSize() {
		return 1;
	}
}
