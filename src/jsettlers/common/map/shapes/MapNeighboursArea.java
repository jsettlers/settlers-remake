package jsettlers.common.map.shapes;

import java.util.Iterator;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;

public class MapNeighboursArea implements IMapArea {
	private static final long serialVersionUID = -6205409596340280969L;

	private final short x;
	private final short y;

	public MapNeighboursArea(ShortPoint2D center) {
		this.x = center.getX();
		this.y = center.getY();
	}

	public MapNeighboursArea(final short x, final short y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean contains(ShortPoint2D position) {
		for (ShortPoint2D pos : this) {
			if (pos.equals(position)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return new NeighbourIterator();
	}

	private class NeighbourIterator implements Iterator<ShortPoint2D> {
		int directionIndex = 0;

		@Override
		public boolean hasNext() {
			return directionIndex < EDirection.values.length;
		}

		@Override
		public ShortPoint2D next() {
			return EDirection.values[directionIndex++].getNextHexPoint(x, y);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
