/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.ai.highlevel;

import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.coordinates.CoordinateStream;
import jsettlers.common.utils.coordinates.IBooleanCoordinateFunction;

import java.util.Arrays;
import java.util.Iterator;

/**
 * This is a set of points on the map. It is optimized for range queries.
 * 
 * @author Michael Zangl
 *
 */
public class AiPositions implements IMapArea {
	private static final int MIN_SIZE = 16;
	private static final int SHORT_MASK = 0x7fff;
	/**
	 * 
	 */
	private static final long serialVersionUID = -1032477484624659731L;

	/**
	 * Filters a position map.
	 * 
	 * @author Michael Zangl
	 *
	 */
	public interface AiPositionFilter {
		boolean contains(int x, int y);
	}

	public static class CombinedAiPositionFilter implements AiPositionFilter {
		private AiPositionFilter firstFilter;
		private AiPositionFilter secondFilter;

		public CombinedAiPositionFilter(AiPositionFilter firstFilter, AiPositionFilter secondFilter) {
			this.firstFilter = firstFilter;
			this.secondFilter = secondFilter;
		}

		@Override
		public boolean contains(int x, int y) {
			return firstFilter.contains(x, y) && secondFilter.contains(x, y);
		}
	}

	/**
	 * Rates each position. A lower rating means better.
	 * 
	 * @author Michael Zangl
	 *
	 */
	public interface PositionRater {
		int RATE_INVALID = Integer.MAX_VALUE;

		/**
		 * Rates a given position.
		 * 
		 * @param x
		 * @param y
		 * @param currentBestRating
		 *            The best rating found so far, you can return {@link #RATE_INVALID} if yours is worse.
		 * @return The rating or {@link #RATE_INVALID} if this position is not possible.
		 */
		int rate(int x, int y, int currentBestRating);
	}

	private class PositionsIterator implements Iterator<ShortPoint2D> {
		private int index;

		@Override
		public boolean hasNext() {
			return index < size;
		}

		@Override
		public ShortPoint2D next() {
			int next = points[index];
			index++;
			return new ShortPoint2D(unpackX(next), unpackY(next));
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private boolean sorted = false;
	private int[] points = new int[MIN_SIZE];
	private int size = 0;

	public void add(int x, int y) {
		if (!contains(x, y)) {
			addNoCollission(x, y);
		}
	}

	/**
	 * Add a position of which we are sure that it is not in this set.
	 * 
	 * @param x
	 * @param y
	 */
	public void addNoCollission(int x, int y) {
		int pos = pack(x, y);
		if (points.length == size) {
			resizeTo(points.length * 2);
		}
		points[size] = pos;
		size++;
		sorted = false;
	}

	public void addAllNoCollision(AiPositions otherAiPositions) {
		int newSize = size + otherAiPositions.size;
		if (newSize > points.length) {
			resizeTo(newSize * 2);
		}
		System.arraycopy(otherAiPositions.points, 0, points, size, otherAiPositions.size);
		size = newSize;
		sorted = false;
	}

	public void remove(int x, int y) {
		ensureSorted();
		int index = indexOf(x, y);
		if (index >= 0) {
			if (index < size - 1) {
				points[index] = points[size - 1];
				sorted = false;
			}
			// TODO: shrink array.
			size--;
		}
	}

	@Override
	public boolean contains(ShortPoint2D position) {
		return contains(position.x, position.y);
	}

	@Override
	public boolean contains(int x, int y) {
		ensureSorted();
		return indexOf(x, y) >= 0;
	}

	private int indexOf(int x, int y) {
		return Arrays.binarySearch(points, 0, size, pack(x, y));
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		ensureSorted();
		return new PositionsIterator();
	}

	private void ensureSorted() {
		if (!sorted) {
			Arrays.sort(points, 0, size);
			sorted = true;
		}
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

	private void resizeTo(int arraySize) {
		points = Arrays.copyOf(points, arraySize);
	}

	public void clear() {
		size = 0;
		points = new int[MIN_SIZE];
	}

	public ShortPoint2D getNearestPoint(ShortPoint2D center, int maxDistance) {
		return getNearestPoint(center, maxDistance, null);
	}

	public ShortPoint2D getNearestPoint(ShortPoint2D center, int maxDistance, AiPositionFilter filter) {
		ensureSorted();
		int resX = -1, resY = -1;
		int median = findClosestIndex(center.x, center.y);
		if (median >= size) {
			median = size - 1;
		}
		int l = median, r = median + 1;
		while (true) {
			int current;
			int rDist = r < size ? unpackX(points[r]) - center.x : maxDistance;
			if (l >= 0 && center.x - unpackX(points[l]) <= rDist) {
				current = points[l];
				l--;
			} else if (r < size && rDist < maxDistance) {
				current = points[r];
				r++;
			} else {
				break;
			}

			int x = unpackX(current);
			int y = unpackY(current);
			if (filter != null && !filter.contains(x, y)) {
				continue;
			}

			int pDist = ShortPoint2D.getOnGridDist(center.x - x, center.y - y);
			if (pDist < maxDistance) {
				resX = x;
				resY = y;
				maxDistance = pDist;
			}
		}

		return resY >= 0 ? new ShortPoint2D(resX, resY) : null;
	}

	public ShortPoint2D get(int index) {
		return new ShortPoint2D(unpackX(points[index]), unpackY(points[index]));
	}

	private int findClosestIndex(int x, int y) {
		return Math.abs(indexOf(x, y));
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size <= 0;
	}

	@Override
	public String toString() {
		final int maxLen = 100;
		StringBuilder pointsStr = new StringBuilder();
		for (ShortPoint2D p : this) {
			if (pointsStr.length() > 0) {
				pointsStr.append(" ");
			}
			if (pointsStr.length() > maxLen) {
				pointsStr.append("...");
				break;
			}
			pointsStr.append(p.x);
			pointsStr.append(",");
			pointsStr.append(p.y);
		}
		return "AiPositions [sorted=" + sorted + ", size=" + size + ", points="
				+ pointsStr + "]";
	}

	public ShortPoint2D getBestRatedPoint(PositionRater rater) {
		// TODO: Parallel ?
		int currentBestRating = PositionRater.RATE_INVALID;
		ShortPoint2D currentBest = null;
		for (int i = 0; i < size; i++) {
			int x = unpackX(points[i]);
			int y = unpackY(points[i]);
			int rating = rater.rate(x, y, currentBestRating);
			if (rating < currentBestRating) {
				currentBestRating = rating;
				currentBest = new ShortPoint2D(x, y);
			}
		}

		return currentBest;
	}

	public CoordinateStream stream() {
		return new CoordinateStream() {
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
		};
	}
}
