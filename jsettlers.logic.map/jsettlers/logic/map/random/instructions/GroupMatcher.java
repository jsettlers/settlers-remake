package jsettlers.logic.map.random.instructions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.map.IMapData;
import jsettlers.common.map.shapes.MapNeighboursArea;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;

public class GroupMatcher extends TileMatcher {

	public GroupMatcher(IMapData grid, int startx, int starty, int distance,
	        LandFilter onLandscape, Random random) {
		super(grid, startx, starty, distance, onLandscape, random);
	}

	@Override
	public Iterator<ISPosition2D> iterator() {
		return new GroupMatcherIterator();
	}

	private class GroupMatcherIterator implements Iterator<ISPosition2D> {

		Queue<ISPosition2D> possible =
		        new ConcurrentLinkedQueue<ISPosition2D>();

		Set<ISPosition2D> found = new HashSet<ISPosition2D>();

		private ISPosition2D current;

		public GroupMatcherIterator() {
			ShortPoint2D pos = new ShortPoint2D(startx, starty);
			possible.offer(pos);
			found.add(pos);
			computeNext();
		}

		@Override
		public boolean hasNext() {
			return current != null;
		}

		private void computeNext() {
			do {
				if (possible.isEmpty()) {
					current = null;
					break;
				}
				current = possible.poll();
				for (ISPosition2D pos : new MapShapeFilter(new MapNeighboursArea(
				        current), grid.getWidth(), grid.getHeight())) {
					if (!found.contains(pos)
					        && Math.hypot(pos.getX() - startx, pos.getY()
					                - starty) < distance) {
						possible.offer(pos);
						found.add(pos);
					}
				}
			} while (!isPlaceable(current));

		}

		@Override
		public ISPosition2D next() {
			ISPosition2D next = this.current;
			computeNext();
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
