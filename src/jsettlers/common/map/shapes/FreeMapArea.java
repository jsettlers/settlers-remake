package jsettlers.common.map.shapes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.SRectangle;

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

	private final List<ISPosition2D> positions;
	private final short xOffset;
	private final short yOffset;
	private final boolean[][] areaMap;
	private final int width;
	private final int height;

	/**
	 * @param positions
	 *            the positions this map area shall contain.
	 */
	public FreeMapArea(List<ISPosition2D> positions) {
		assert positions.size() > 0 : "positions must contain at least one value!!";

		this.positions = positions;
		SRectangle bounds = getBounds(positions);

		xOffset = bounds.getXMin();
		yOffset = bounds.getYMin();
		width = bounds.getWidth() + 1;
		height = bounds.getHeight() + 1;

		areaMap = new boolean[width][height];
		setPositionsToMap(areaMap, positions);
	}

	/**
	 * Creates a free map area by converting the relative points to absolute ones.
	 * 
	 * @param pos
	 *            The origin for the relative points
	 * @param relativePoints
	 *            The relative points
	 */
	public FreeMapArea(ISPosition2D pos, RelativePoint[] relativePoints) {
		this(convertRelative(pos, relativePoints));
	}

	private final static ArrayList<ISPosition2D> convertRelative(ISPosition2D pos, RelativePoint[] relativePoints) {
		ArrayList<ISPosition2D> list = new ArrayList<ISPosition2D>();

		for (RelativePoint relative : relativePoints) {
			list.add(relative.calculatePoint(pos));
		}
		return list;
	}

	private final void setPositionsToMap(boolean[][] areaMap, List<ISPosition2D> positions) {
		for (ISPosition2D curr : positions) {
			areaMap[getMapX(curr)][getMapY(curr)] = true;
		}
	}

	final int getMapY(ISPosition2D pos) {
		return pos.getY() - yOffset;
	}

	final int getMapX(ISPosition2D pos) {
		return pos.getX() - xOffset;
	}

	private final SRectangle getBounds(List<ISPosition2D> positions) {
		short xMin = Short.MAX_VALUE, xMax = 0, yMin = Short.MAX_VALUE, yMax = 0;

		for (ISPosition2D curr : positions) {
			short x = curr.getX();
			short y = curr.getY();
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
	public final boolean contains(ISPosition2D pos) {
		return isValidPos(pos) && areaMap[getMapX(pos)][getMapY(pos)];
	}

	private final boolean isValidPos(ISPosition2D pos) {
		int dx = pos.getX() - xOffset;
		int dy = pos.getY() - yOffset;
		return dx >= 0 && dy >= 0 && dx < width && dy < height;
	}

	@Override
	public final Iterator<ISPosition2D> iterator() {
		return new FreeMapAreaIterator(this);
	}

	public final int size() {
		return positions.size();
	}

	public final ISPosition2D get(int i) {
		return positions.get(i);
	}

	public final boolean isEmpty() {
		return positions.isEmpty();
	}

	final void setPosition(ISPosition2D pos, boolean value) {
		areaMap[getMapX(pos)][getMapY(pos)] = value;
	}

	private final static class FreeMapAreaIterator implements Iterator<ISPosition2D> {

		private final FreeMapArea freeMapArea;
		private final Iterator<ISPosition2D> iterator;
		private ISPosition2D currPos;

		public FreeMapAreaIterator(FreeMapArea freeMapArea) {
			this.freeMapArea = freeMapArea;
			this.iterator = freeMapArea.positions.iterator();
		}

		@Override
		public final boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public final ISPosition2D next() {
			currPos = iterator.next();
			return currPos;
		}

		@Override
		public final void remove() {
			iterator.remove();
			freeMapArea.setPosition(currPos, false);
		}

	}

}
