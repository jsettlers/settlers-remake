package jsettlers.logic.algorithms.path.dijkstra;

import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.InvalidStartPositionException;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.algorithms.path.astar.HexAStar;

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
	private final HexAStar aStar;

	public DijkstraAlgorithm(IDijkstraPathMap map, HexAStar aStar) {
		this.map = map;
		this.aStar = aStar;
		this.height = map.getHeight();
		this.width = map.getWidth();

	}

	public final Path find(final IPathCalculateable requester, final short cX, final short cY, final short minRadius, final short maxRadius,
			final ESearchType type) {
		if (!isInBounds(cX, cY)) {
			throw new InvalidStartPositionException("dijkstra center position is not in bounds!", cX, cY);
		}

		// TODO @Michael find out how much the radius must be bigger to be larger than the circle
		MapCircle circle = new MapCircle(cX, cY, maxRadius * MAX_RADIUS_MULTIPLIER);

		for (short radius = minRadius; radius < maxRadius; radius++) {
			short x = cX, y = (short) (cY - radius);
			for (byte direction = 0; direction < 6; direction++) {
				byte dx = directionIncreaseX[direction];
				byte dy = directionIncreaseY[direction];
				for (short length = 0; length < radius; length++) {
					x += dx;
					y += dy;
					if (circle.contains(x, y) && isInBounds(x, y)) {
						map.setDijkstraSearched(x, y);
						if (map.fitsSearchType(x, y, type, requester)) {
							Path path = findPathTo(requester, x, y);
							if (path != null)
								return path;
						}
					}
				}
			}
		}

		return null;
	}

	private final Path findPathTo(IPathCalculateable requester, short tx, short ty) {
		ISPosition2D pos = requester.getPos();
		return aStar.findPath(requester, pos.getX(), pos.getY(), tx, ty);
	}

	private final boolean isInBounds(short x, short y) {
		return 0 <= x && x < width && 0 <= y && y < height;
	}

	public static class DijkstraContinuableRequest {
		final short minRadius;
		final short maxRadius;
		final IPathCalculateable requester;
		final short cX;
		final short cY;
		final ESearchType searchType;

		short radius;

		public DijkstraContinuableRequest(final IPathCalculateable requester, short cX, short cY, short minRadius, short maxRadius,
				ESearchType searchType) {
			this.requester = requester;
			this.cX = cX;
			this.cY = cY;
			this.minRadius = minRadius;
			this.maxRadius = maxRadius;
			this.searchType = searchType;

			this.radius = 0;
		}

		final short getRadiusSteps() {
			return 3;
		}

		public boolean isCenterAt(ISPosition2D pos) {
			return pos != null && pos.getX() == cX && pos.getY() == cY;
		}

		void setRadius(short radius) {
			this.radius = (short) (radius - this.minRadius + 1);
		}
	}

	public final Path find(DijkstraContinuableRequest request) {
		if (!isInBounds(request.cX, request.cY)) {
			throw new InvalidStartPositionException("dijkstra center position is not in bounds!", request.cX, request.cY);
		}

		// TODO @Michael find out how much the radius must be bigger to be larger than the circle
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
						if (map.fitsSearchType(x, y, request.searchType, request.requester)) {
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
