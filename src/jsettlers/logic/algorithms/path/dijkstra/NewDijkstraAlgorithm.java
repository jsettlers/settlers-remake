package jsettlers.logic.algorithms.path.dijkstra;

import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.algorithms.path.astar.HexAStar;
import jsettlers.logic.algorithms.path.wrapper.InvalidStartPositionException;

/**
 * this class implements a strict dijkstra algorithm
 * 
 * @author Andreas Eberle
 * 
 */
public class NewDijkstraAlgorithm {

	private final INewDijkstraPathMap map;
	private final short height, width;
	private final HexAStar aStar;

	public NewDijkstraAlgorithm(INewDijkstraPathMap map, HexAStar aStar) {
		this.map = map;
		this.aStar = aStar;
		height = map.getHeight();
		width = map.getWidth();
	}

	private static final byte[] directionIncreaseX = { -1, 0, 1, 1, 0, -1 };
	private static final byte[] directionIncreaseY = { 0, -1, -1, 0, 1, 1 };

	public synchronized Path find(final IPathCalculateable requester, final short cX, final short cY, final short minRadius, final short maxRadius,
			final ESearchType type) {
		if (!isInBounds(cX, cY)) {
			throw new InvalidStartPositionException(cX, cY);
		}

		for (short radius = minRadius; radius < maxRadius; radius++) {
			short x = cX, y = (short) (cY + radius);
			for (byte direction = 0; direction < 6; direction++) {
				byte dx = directionIncreaseX[direction];
				byte dy = directionIncreaseY[direction];
				for (short length = 0; length < radius; length++) {
					x += dx;
					y += dy;
					map.setDijkstraSearched(x, y);
					if (isInBounds(x, y) && map.fitsSearchType(x, y, type, requester)) {
						Path path = findPath(requester, x, y);
						if (path != null)
							return path;
					}
				}
			}
		}

		return null;
	}

	private Path findPath(IPathCalculateable requester, short tx, short ty) {
		ISPosition2D pos = requester.getPos();
		return aStar.findPath(requester, pos.getX(), pos.getY(), tx, ty);
	}

	private boolean isInBounds(short x, short y) {
		return 0 <= x && x <= width && 0 <= y && y <= height;
	}
}
