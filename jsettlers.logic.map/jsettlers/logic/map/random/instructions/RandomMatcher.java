package jsettlers.logic.map.random.instructions;

import java.util.Iterator;
import java.util.Random;

import jsettlers.common.map.IMapData;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;

public class RandomMatcher extends TileMatcher {
	public RandomMatcher(IMapData grid, int startx, int starty, int distance,
	        LandFilter onLandscape, Random random) {
		super(grid, startx, starty, distance, onLandscape, random);
	}

	@Override
	public Iterator<ISPosition2D> iterator() {
		return new RandomMatcherIterator();
	}

	private class RandomMatcherIterator implements Iterator<ISPosition2D> {

		private static final int MAX_TRIES = 50;
		private ISPosition2D current;

		public RandomMatcherIterator() {
			computeNext();
		}

		@Override
		public boolean hasNext() {
			return current != null;
		}

		private void computeNext() {
			int tries = 0;
			do {
				current =
				        new ShortPoint2D(
				                getRandAround(startx, distance, random),
				                getRandAround(starty, distance, random));
				tries++;
			} while (tries < MAX_TRIES && !isPlaceable(current));

			if (tries >= MAX_TRIES) {
				current = null;
			}
		}

		private int getRandAround(int center, int vary, Random random) {
			return center - vary + random.nextInt(vary * 2 + 1);
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
