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

import jsettlers.common.position.SRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.coordinates.CoordinateStream;
import jsettlers.common.utils.coordinates.IBooleanCoordinateFunction;
import jsettlers.common.utils.coordinates.ICoordinatePredicate;

/**
 * This class represents a circular area of the map.
 * <p>
 * It contains all elements whose distance to the center is smaller or equal to radius.
 * <p>
 * Geometry calculations (a is the constant {@link MapCircle#Y_SCALE}:
 * <p>
 * <img src="doc-files/MapCircle-1.png">
 *
 * @author michael
 */
public final class MapCircle implements IMapArea {
	private static final long serialVersionUID = 1L;

	private final float radius;
	private final short centerX;
	private final short centerY;

	/**
	 * Factor so that d((0,0), (1,1)) is almost 1.
	 */
	public final static float Y_SCALE = (float) Math.sqrt(3) / 2.0f * .999999f;

	public MapCircle(ShortPoint2D pos, float radius) {
		this(pos.x, pos.y, radius);
	}

	public MapCircle(int centerX, int centerY, float radius) {
		this.centerX = (short) centerX;
		this.centerY = (short) centerY;
		this.radius = radius;
	}

	@Override
	public final boolean contains(ShortPoint2D position) {
		return contains(position.x, position.y);
	}

	public final boolean contains(int x, int y) {
		float distance = squaredDistanceToCenter(x, y);
		return distance <= radius * radius;
	}

	@Override
	public final MapCircleIterator iterator() {
		return new MapCircleIterator(this);
	}

	/**
	 * Gets the distance of map coordinates to the center.
	 *
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate
	 * @return The distance to the center of this circle, so that the tiles around the center all have distance 1.
	 */
	public final float squaredDistanceToCenter(int x, int y) {
		return getSquaredDistance(x - centerX, y - centerY);
	}

	/**
	 * Gives the squared distance for given delta x and delta y
	 *
	 * @param dx
	 * @param dy
	 * @return
	 */
	public static float getSquaredDistance(int dx, int dy) {
		return (.25f + Y_SCALE * Y_SCALE) * dy * dy + dx * dx - dx * dy;
	}

	public static final float getDistance(int x1, int y1, int x2, int y2) {
		float squared = getDistanceSquared(x1, y1, x2, y2);
		return (float) Math.sqrt(squared);
	}

	public static final float getDistanceSquared(int x1, int y1, int x2, int y2) {
		int dx = x1 - x2;
		int dy = y1 - y2;
		return getSquaredDistance(dx, dy);
	}

	/**
	 * Gets the half width of a line, roundend.
	 *
	 * @param relativeY
	 *            The x coordinate of the line relative to the center
	 * @return The width of the line, NAN if the line is outside the circle.
	 */
	protected final float getHalfLineWidth(int relativeY) {
		return calculateHalfLineWidth(radius, relativeY);
	}

	private static float calculateHalfLineWidth(float radius, int relativeY) {
		double maximum = Math.sqrt(radius * radius - relativeY * MapCircle.Y_SCALE * relativeY * MapCircle.Y_SCALE);
		if (relativeY % 2 == 0) {
			// round to tiles.
			return (float) Math.floor(maximum);
		} else {
			// uneven line, round x to *.5
			return (float) (Math.floor(maximum + .5) - .5);
		}
	}

	public final double distanceToCenter(int x, int y) {
		return Math.sqrt(squaredDistanceToCenter(x, y));
	}

	public final float getRadius() {
		return radius;
	}

	public final short getCenterX() {
		return centerX;
	}

	public final short getCenterY() {
		return centerY;
	}

	public ShortPoint2D getCenter() {
		return new ShortPoint2D(centerX, centerY);
	}

	/**
	 * Calculates the outer borders of this map circle. That means that all positions of this circle are inside the returned rectangle.
	 *
	 * @return
	 */
	public SRectangle getBorders() {
		short yRadius = (short) (radius / MapCircle.Y_SCALE + 1); // +1 to make sure all positions are in
		short halfLineWidth = (short) (radius * 1.2f);

		return new SRectangle((short) (centerX - halfLineWidth), (short) (centerY - yRadius), (short) (centerX + halfLineWidth),
				(short) (centerY + yRadius));
	}

	@Override
	public CoordinateStream stream() {
		return stream(centerX, centerY, radius);
	}

	public static CoordinateStream stream(ShortPoint2D center, float radius) {
		return stream(center.x, center.y, radius);
	}

	public static CoordinateStream stream(int centerX, int centerY, float radius) {
		return new CoordinateStream() {
			@Override
			public boolean iterate(IBooleanCoordinateFunction function) {
				int currentYOffset = -(int) (radius / MapCircle.Y_SCALE);
				float currentHalfLineWidth = calculateHalfLineWidth(radius, currentYOffset);
				float currentXOffset = -currentHalfLineWidth;

				int maxInternalY = (int) Math.ceil(radius / MapCircle.Y_SCALE);

				while (currentYOffset < maxInternalY) {
					int x = (int) Math.ceil(.5f * currentYOffset + currentXOffset) + centerX;
					int y = currentYOffset + centerY;

					if (!function.apply(x, y)) {
						return false;
					}

					currentXOffset++;
					if (currentXOffset > currentHalfLineWidth) {
						// next line
						currentYOffset++;
						currentHalfLineWidth = calculateHalfLineWidth(radius, currentYOffset);
						currentXOffset = -currentHalfLineWidth;
					}
				}
				return true;
			}
		};
	}

	public CoordinateStream streamBorder() {
		return streamBorder(centerX, centerY, radius);
	}

	public static CoordinateStream streamBorder(int centerX, int centerY, float radius) {
		return stream(centerX, centerY, radius).filter(getBorderPredicate(centerX, centerY, radius));
	}

	private static ICoordinatePredicate getBorderPredicate(int centerX, int centerY, float radius) {
		return (x, y) -> {
			float prevLineWidth = calculateHalfLineWidth(radius, y - centerY - 1);
			float nextLineWidth = calculateHalfLineWidth(radius, y - centerY + 1);
			float xDistToCenter = Math.abs(-x - .5f * (centerY - y) + centerX);

			// DO NOT INLINE ! (NOT) important because of potential NAN of line width
			return !(xDistToCenter < prevLineWidth && xDistToCenter < nextLineWidth);
		};
	}
}
