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
package jsettlers.algorithms.path.dijkstra;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Set;

import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.algorithms.path.InvalidStartPositionException;
import jsettlers.algorithms.path.Path;
import jsettlers.algorithms.path.astar.AbstractAStar;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ShortPoint2D;

/**
 * this class implements a strict dijkstra algorithm
 * 
 * @author Andreas Eberle
 * 
 */
public final class DijkstraAlgorithm {
	private static final byte[] directionIncreaseX = { -1, 0, 1, 1, 0, -1 };
	private static final byte[] directionIncreaseY = { 0, 1, 1, 0, -1, -1 };
	private static final float MAX_RADIUS_MULTIPLIER = 1f / MapCircle.Y_SCALE;

	private final IDijkstraPathMap map;
	private final short height, width;
	private final AbstractAStar aStar;

	public DijkstraAlgorithm(IDijkstraPathMap map, AbstractAStar aStar, short width, short height) {
		this.map = map;
		this.aStar = aStar;
		this.width = width;
		this.height = height;
	}

	public final Path find(final IPathCalculatable requester, final short cX, final short cY, final short minRadius, final short maxRadius,
			final ESearchType type) {
		if (!isInBounds(cX, cY)) {
			throw new InvalidStartPositionException("dijkstra center position is not in bounds!", cX, cY);
		}

		// check center position (special case for minRadius <= 0
		if (minRadius <= 0) {
			map.setDijkstraSearched(cX, cY);
			if (map.fitsSearchType(cX, cY, type, requester)) {
				Path path = findPathTo(requester, cX, cY);
				if (path != null)
					return path;
			}
		}

		for (short radius = minRadius; radius < maxRadius; radius++) {
			short x = cX, y = (short) (cY - radius);
			for (byte direction = 0; direction < 6; direction++) {
				byte dx = directionIncreaseX[direction];
				byte dy = directionIncreaseY[direction];
				for (short length = 0; length < radius; length++) {
					x += dx;
					y += dy;
					if (isInBounds(x, y)) {
						map.setDijkstraSearched(x, y);
						if (map.fitsSearchType(x, y, type, requester)) {
							Path path = findPathTo(requester, x, y);
							if (path != null) {
								return path;
							}
						}
					}
				}
			}
		}

		return null;
	}

	private final Path findPathTo(IPathCalculatable requester, short tx, short ty) {
		ShortPoint2D pos = requester.getPosition();
		return aStar.findPath(requester, pos.x, pos.y, tx, ty);
	}

	private final boolean isInBounds(short x, short y) {
		return 0 <= x && x < width && 0 <= y && y < height;
	}

	public final static class DijkstraContinuableRequest implements Serializable {
		private static final long serialVersionUID = -1350601280043056439L;

		final short minRadius;
		final short maxRadius;
		final IPathCalculatable requester;
		final short cX;
		final short cY;
		Set<ESearchType> searchTypes;

		short radius;

		public DijkstraContinuableRequest(final IPathCalculatable requester, short cX, short cY, short minRadius, short maxRadius) {
			this.requester = requester;
			this.cX = cX;
			this.cY = cY;
			this.minRadius = minRadius;
			this.maxRadius = maxRadius;
			this.searchTypes = EnumSet.noneOf(ESearchType.class);

			this.radius = 0;
		}

		final short getRadiusSteps() {
			return 6;
		}

		void setRadius(short radius) {
			this.radius = (short) (radius - this.minRadius + 1);
		}

		public void setSearchTypes(Set<ESearchType> searchTypes) {
			if (!this.searchTypes.equals(searchTypes)) {
				this.searchTypes = searchTypes;
				radius = 0;
			}
		}

		public void reset() {
			radius = 0;
		}
	}

	public final Path find(DijkstraContinuableRequest request) {
		if (!isInBounds(request.cX, request.cY)) {
			throw new InvalidStartPositionException("dijkstra center position is not in bounds!", request.cX, request.cY);
		}

		MapCircle circle = new MapCircle(request.cX, request.cY, request.maxRadius * MAX_RADIUS_MULTIPLIER);

		short radiusSteps = request.getRadiusSteps();
		short radius = 1;

		for (short deltaRadius = 0; deltaRadius < radiusSteps; deltaRadius++) {
			radius = (short) ((deltaRadius + request.radius) % request.maxRadius + request.minRadius);
			short x = request.cX, y = (short) (request.cY - radius);

			for (byte direction = 0; direction < 6; direction++) {
				byte dx = directionIncreaseX[direction];
				byte dy = directionIncreaseY[direction];
				for (short length = 0; length < radius; length++) {
					x += dx;
					y += dy;
					if (circle.contains(x, y) && isInBounds(x, y)) {
						map.setDijkstraSearched(x, y);
						if (map.fitsSearchType(x, y, request.searchTypes, request.requester)) {
							Path path = findPathTo(request.requester, x, y);
							if (path != null) {
								request.setRadius(radius);
								return path;
							}
						}
					}
				}
			}
		}

		request.setRadius(radius);
		return null;
	}
}
