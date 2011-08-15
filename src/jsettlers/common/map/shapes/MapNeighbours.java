package jsettlers.common.map.shapes;

import java.util.Iterator;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ISPosition2D;

public class MapNeighbours implements IMapArea {
	private final ISPosition2D center;

	public MapNeighbours(ISPosition2D center) {
		this.center = center;
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
			return EDirection.values()[directionIndex++]
			        .getNextHexPoint(center);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
