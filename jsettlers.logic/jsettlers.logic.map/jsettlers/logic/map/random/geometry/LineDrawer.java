package jsettlers.logic.map.random.geometry;

import java.util.Iterator;
import java.util.List;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;

public class LineDrawer implements Iterable<ShortPoint2D> {

	private final List<Point> points;

	public LineDrawer(List<Point> points) {
		if (points.size() < 2) {
			throw new IllegalArgumentException("There have to be at least 2 points in a line");
		}
		this.points = points;
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return new LineIterator();
	}

	private class LineIterator implements Iterator<ShortPoint2D> {
		Iterator<Point> pointIt;
		ShortPoint2D currentTarget;
		ShortPoint2D current;

		LineIterator() {
			pointIt = points.iterator();
			Point first = pointIt.next();
			current = new ShortPoint2D(first.getX(), first.getY());
			relaodTarget();
		}

		private void relaodTarget() {
			if (pointIt.hasNext()) {
				Point target = pointIt.next();
				currentTarget = new ShortPoint2D(target.getX(), target.getY());
			} else {
				currentTarget = null;
			}
		}

		@Override
		public boolean hasNext() {
			return currentTarget != null;
		}

		@Override
		public ShortPoint2D next() {
			ShortPoint2D next = current;
			if (current.equals(currentTarget)) {
				relaodTarget();
			}
			if (currentTarget != null) {
				EDirection dir = EDirection.getApproxDirection(current, currentTarget);
				current = dir.getNextHexPoint(current);
			}
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}
