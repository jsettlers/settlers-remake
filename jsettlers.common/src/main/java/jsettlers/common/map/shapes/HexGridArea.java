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
package jsettlers.common.map.shapes;

import java.io.Serializable;
import java.util.Iterator;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;

/**
 * Represents a hexagon on the grid.
 * 
 * @author Andreas Eberle
 * 
 */
public final class HexGridArea implements IMapArea {
	private static final long serialVersionUID = -2218632675269689379L;
	final short cX;
	final short cY;
	final short startRadius;
	final short maxRadius;

	/**
	 * Hexagon area from including {@link #startRadius} to including {@link #maxRadius}
	 * 
	 * @param cX
	 *            center x
	 * @param cY
	 *            center y
	 * @param startRadius
	 *            inclusive inner radius
	 * @param maxRadius
	 *            inclusive outer radius
	 */
	public HexGridArea(int cX, int cY, int startRadius, int maxRadius) {
		this.cX = (short) cX;
		this.cY = (short) cY;
		this.startRadius = (short) startRadius;
		this.maxRadius = (short) maxRadius;
	}

	@Override
	public boolean contains(ShortPoint2D position) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public HexGridAreaIterator iterator() {
		return new HexGridAreaIterator(this);
	}

	public static final class HexGridAreaIterator implements Iterator<ShortPoint2D>, Serializable {
		private static final long serialVersionUID = -8760653162789299782L;

		private static final byte[] directionIncreaseX = { EDirection.SOUTH_EAST.gridDeltaX, EDirection.SOUTH_WEST.gridDeltaX,
				EDirection.WEST.gridDeltaX, EDirection.NORTH_WEST.gridDeltaX, EDirection.NORTH_EAST.gridDeltaX, EDirection.EAST.gridDeltaX };
		private static final byte[] directionIncreaseY = { EDirection.SOUTH_EAST.gridDeltaY, EDirection.SOUTH_WEST.gridDeltaY,
				EDirection.WEST.gridDeltaY, EDirection.NORTH_WEST.gridDeltaY, EDirection.NORTH_EAST.gridDeltaY, EDirection.EAST.gridDeltaY };
		private static final int MAX_DIRECTIONS_IDX = EDirection.NUMBER_OF_DIRECTIONS - 1;

		private final HexGridArea hexGridArea;
		private short radius;
		private short x;
		private short y;
		private int direction;
		private short length = 1;

		public HexGridAreaIterator(HexGridArea hexGridArea) {
			this.hexGridArea = hexGridArea;
			radius = hexGridArea.startRadius;

			x = hexGridArea.cX;
			y = (short) (hexGridArea.cY - radius); // radius * NORTH_EAST

			if (hexGridArea.startRadius == 0) {
				direction = EDirection.NUMBER_OF_DIRECTIONS;
			} else {
				direction = 0;
				x += EDirection.SOUTH_EAST.gridDeltaX;
				y += EDirection.SOUTH_EAST.gridDeltaY;
			}
		}

		@Override
		public boolean hasNext() {
			return radius <= hexGridArea.maxRadius;
		}

		public short getRadiusOfNext() {
			return radius;
		}

		@Override
		public ShortPoint2D next() {
			ShortPoint2D result = new ShortPoint2D(x, y);

			if (length >= radius) {
				length = 0;
				direction++;

				if (direction >= EDirection.NUMBER_OF_DIRECTIONS) {
					x += directionIncreaseX[MAX_DIRECTIONS_IDX];
					y += directionIncreaseY[MAX_DIRECTIONS_IDX];

					direction = 0;
					length = 1;
					radius++;

					return result;
				}
			}
			length++;

			x += directionIncreaseX[direction];
			y += directionIncreaseY[direction];

			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("not implemented!");
		}
	}
}
