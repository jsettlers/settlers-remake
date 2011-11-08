package jsettlers.common.map.shapes;

import java.util.Iterator;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ISPosition2D;

public class MapNeighboursArea implements IMapArea {
	private static final long serialVersionUID = -6205409596340280969L;

	private final short x;
	private final short y;

	public MapNeighboursArea(ISPosition2D center) {
		this.x = center.getX();
		this.y = center.getY();
	}

	public MapNeighboursArea(final short x, final short y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean contains(ISPosition2D position) {
		for (ISPosition2D pos : this) {
			if (pos.equals(position)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<ISPosition2D> iterator() {
		return new NeighbourIterator();
	}

	private class NeighbourIterator implements Iterator<ISPosition2D> {
		int directionIndex = 0;

		@Override
		public boolean hasNext() {
			return directionIndex < EDirection.values().length;
		}

		@Override
		public ISPosition2D next() {
			return EDirection.values()[directionIndex++].getNextHexPoint(x, y);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
