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
package jsettlers.common.map.shapes;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.coordinates.CoordinateStream;
import jsettlers.common.utils.coordinates.IBooleanCoordinateFunction;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Represents a hexagon on the grid.
 *
 * @author Andreas Eberle
 */
public final class HexGridArea implements IMapArea {

	private static final int MAX_DIRECTIONS_IDX = EDirection.NUMBER_OF_DIRECTIONS - 1;
	private static final byte[] DIRECTION_INCREASE_X = { EDirection.SOUTH_EAST.gridDeltaX, EDirection.SOUTH_WEST.gridDeltaX,
			EDirection.WEST.gridDeltaX, EDirection.NORTH_WEST.gridDeltaX, EDirection.NORTH_EAST.gridDeltaX, EDirection.EAST.gridDeltaX };
	private static final byte[] DIRECTION_INCREASE_Y = { EDirection.SOUTH_EAST.gridDeltaY, EDirection.SOUTH_WEST.gridDeltaY,
			EDirection.WEST.gridDeltaY, EDirection.NORTH_WEST.gridDeltaY, EDirection.NORTH_EAST.gridDeltaY, EDirection.EAST.gridDeltaY };

	private static final long serialVersionUID = -2218632675269689379L;
	final short cX;
	final short cY;
	final short startRadius;
	final short maxRadius;

	/**
	 * Hexagon area from including {@link #startRadius} to including {@link #maxRadius}
	 *
	 * @param centerX
	 *            center x
	 * @param centerY
	 *            center y
	 * @param startRadius
	 *            inclusive inner radius
	 * @param maxRadius
	 *            inclusive outer radius
	 */
	public HexGridArea(int centerX, int centerY, int startRadius, int maxRadius) {
		this.cX = (short) centerX;
		this.cY = (short) centerY;
		this.startRadius = (short) startRadius;
		this.maxRadius = (short) maxRadius;
	}

	public HexGridArea(int centerX, int centerY, int radius) {
		this(centerX, centerY, 0, radius);
	}

	public HexGridArea(ShortPoint2D center, int radius) {
		this(center.x, center.y, radius);
	}

	@Override
	public boolean contains(ShortPoint2D position) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public boolean contains(int x, int y) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public HexGridAreaIterator iterator() {
		return new HexGridAreaIterator(this);
	}

	public static final class HexGridAreaIterator implements Iterator<ShortPoint2D>, Serializable {
		private static final long serialVersionUID = -8760653162789299782L;

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

		@Override
		public ShortPoint2D next() {
			ShortPoint2D result = new ShortPoint2D(x, y);

			if (length >= radius) {
				length = 0;
				direction++;

				if (direction >= EDirection.NUMBER_OF_DIRECTIONS) {
					x += DIRECTION_INCREASE_X[MAX_DIRECTIONS_IDX];
					y += DIRECTION_INCREASE_Y[MAX_DIRECTIONS_IDX];

					direction = 0;
					length = 1;
					radius++;

					return result;
				}
			}
			length++;

			x += DIRECTION_INCREASE_X[direction];
			y += DIRECTION_INCREASE_Y[direction];

			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("not implemented!");
		}
	}

	@Override
	public CoordinateStream stream() {
		return stream(cX, cY, startRadius, maxRadius);
	}

	public static CoordinateStream stream(int cX, int cY, int startRadius, int maxRadius) {
		return new CoordinateStream() {
			@Override
			public boolean iterate(IBooleanCoordinateFunction function) {
				if (startRadius == 0) {
					if (!function.apply(cX, cY)) {
						return false;
					}
				}

				int x = cX;
				int y = cY - startRadius; // radius * NORTH_EAST

				for (int radius = startRadius; radius <= maxRadius; radius++) {
					for (int direction = 0; direction < EDirection.NUMBER_OF_DIRECTIONS; direction++) {
						for (int step = 0; step < radius; step++) {
							x += DIRECTION_INCREASE_X[direction];
							y += DIRECTION_INCREASE_Y[direction];

							if (!function.apply(x, y)) {
								return false;
							}
						}
					}
					y--; // go to next radius / go one NORTH_EAST
				}

				return true;
			}
		};
	}

	public CoordinateStream streamBorder() {
		return streamBorder(cX, cY, maxRadius);
	}

	public static CoordinateStream streamBorder(short centerX, short centerY, int radius) {
		return stream(centerX, centerY, radius, radius);
	}

	public static CoordinateStream streamBorder(ShortPoint2D center, int radius) {
		return streamBorder(center.x, center.y, radius);
	}
}
