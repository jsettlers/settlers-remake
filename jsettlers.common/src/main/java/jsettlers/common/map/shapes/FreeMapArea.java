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

import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.SRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.coordinates.CoordinateStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class gives a fast lookup (in O(1)) for contains if a MapArea is given by a list of n positions.<br>
 * This class should only be used if the given positions are NOT distributed over big parts of the map. They should be positioned quite close to each
 * other.
 * <p />
 * The iterator is able to remove positions from the area!
 * 
 * @author Andreas Eberle
 */
public final class FreeMapArea implements IMapArea {
	private static final long serialVersionUID = 6331090134655931952L;

	private final List<ShortPoint2D> positions;
	private final int xOffset;
	private final int yOffset;
	private final boolean[][] areaMap;
	private final int width;
	private final int height;
	private final ShortPoint2D upperLeftPosition;

	/**
	 * @param positions
	 *            the positions this map area will contain.
	 */
	public FreeMapArea(List<ShortPoint2D> positions) {
		assert positions.size() > 0 : "positions must contain at least one value!!";

		this.positions = positions;
		SRectangle bounds = getBounds(positions);

		this.xOffset = bounds.xMin;
		this.yOffset = bounds.yMin;
		this.width = bounds.getWidth() + 1;
		this.height = bounds.getHeight() + 1;

		this.areaMap = new boolean[width][height];
		this.upperLeftPosition = setPositionsToMap(areaMap, positions);
	}

	/**
	 * 
	 * @param positions
	 * @param minX
	 *            Minimum x value in the list of positions.
	 * @param minY
	 *            Minimum y value in the list of positions.
	 * @param width
	 *            minX + width -1 is the maximum x value in the list of positions.
	 * @param height
	 *            minY + height -1 is the maximum y value in the list of positions.
	 */
	public FreeMapArea(List<ShortPoint2D> positions, int minX, int minY, int width, int height) {
		assert positions.size() > 0 : "positions must contain at least one value!!";

		this.positions = positions;
		this.xOffset = minX;
		this.yOffset = minY;
		this.width = width;
		this.height = height;

		this.areaMap = new boolean[width][height];
		this.upperLeftPosition = setPositionsToMap(areaMap, positions);
	}

	/**
	 * Creates a free map area by converting the relative points to absolute ones.
	 * 
	 * @param pos
	 *            The origin for the relative points
	 * @param relativePoints
	 *            The relative points
	 */
	public FreeMapArea(ShortPoint2D pos, RelativePoint[] relativePoints) {
		this(convertRelative(pos, relativePoints));
	}

	private final static ArrayList<ShortPoint2D> convertRelative(ShortPoint2D pos, RelativePoint[] relativePoints) {
		ArrayList<ShortPoint2D> list = new ArrayList<>();

		for (RelativePoint relative : relativePoints) {
			list.add(relative.calculatePoint(pos));
		}
		return list;
	}

	/**
	 * Sets the positions to the map and returns the upper left position
	 * 
	 * @param areaMap
	 * @param positions
	 * @return
	 */
	private final ShortPoint2D setPositionsToMap(boolean[][] areaMap, List<ShortPoint2D> positions) {
		if (positions.isEmpty()) {
			return null;
		}

		ShortPoint2D upperLeft = positions.get(0);

		for (ShortPoint2D curr : positions) {
			areaMap[getMapX(curr)][getMapY(curr)] = true;

			if (curr.y < upperLeft.y || curr.y == upperLeft.y && curr.x < upperLeft.x) {
				upperLeft = curr;
			}
		}

		return upperLeft;
	}

	final int getMapY(ShortPoint2D pos) {
		return pos.y - yOffset;
	}

	final int getMapX(ShortPoint2D pos) {
		return pos.x - xOffset;
	}

	private final SRectangle getBounds(List<ShortPoint2D> positions) {
		short xMin = Short.MAX_VALUE, xMax = 0, yMin = Short.MAX_VALUE, yMax = 0;

		for (ShortPoint2D curr : positions) {
			short x = curr.x;
			short y = curr.y;
			if (x < xMin)
				xMin = x;
			if (x > xMax)
				xMax = x;

			if (y < yMin)
				yMin = y;
			if (y > yMax)
				yMax = y;
		}

		return new SRectangle(xMin, yMin, xMax, yMax);
	}

	@Override
	public final boolean contains(ShortPoint2D pos) {
		return contains(pos.x, pos.y);
	}

	public boolean contains(int x, int y) {
		int dx = x - xOffset;
		int dy = y - yOffset;

		return isValidMapPos(dx, dy) && areaMap[dx][dy];
	}

	private final boolean isValidMapPos(int dx, int dy) {
		return dx >= 0 && dy >= 0 && dx < width && dy < height;
	}

	@Override
	public final Iterator<ShortPoint2D> iterator() {
		return new FreeMapAreaIterator(this);
	}

	public final int size() {
		return positions.size();
	}

	public final ShortPoint2D get(int i) {
		return positions.get(i);
	}

	public final boolean isEmpty() {
		return positions.isEmpty();
	}

	final void setPosition(ShortPoint2D pos, boolean value) {
		areaMap[getMapX(pos)][getMapY(pos)] = value;
	}

	public ShortPoint2D getUpperLeftPosition() {
		return upperLeftPosition;
	}

	private final static class FreeMapAreaIterator implements Iterator<ShortPoint2D> {

		private final FreeMapArea freeMapArea;
		private final Iterator<ShortPoint2D> iterator;
		private ShortPoint2D currPos;

		public FreeMapAreaIterator(FreeMapArea freeMapArea) {
			this.freeMapArea = freeMapArea;
			this.iterator = freeMapArea.positions.iterator();
		}

		@Override
		public final boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public final ShortPoint2D next() {
			currPos = iterator.next();
			return currPos;
		}

		@Override
		public final void remove() {
			iterator.remove();
			freeMapArea.setPosition(currPos, false);
		}
	}

	@Override
	public CoordinateStream stream() {
		return CoordinateStream.fromList(positions);
	}

}
