package jsettlers.logic.algorithms.fogofwar.circle;

import java.util.Iterator;

import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.map.shapes.MapCircleIterator;
import jsettlers.common.position.ShortPoint2D;

/**
 * Iterator for CachedRelativeMapCircle based on {@link MapCircleIterator}
 * 
 * @author Andreas Eberle
 * 
 */
public final class CachedRelativeCircleIterator implements Iterator<ShortPoint2D> {
	protected int currenty;

	protected float currentLineHalfWidth;
	// x from vertical center line of circle.
	protected float currentx;

	protected final float radius;

	private final CachedRelativeMapCircle circle;

	public CachedRelativeCircleIterator(CachedRelativeMapCircle circle) {
		this.circle = circle;
		radius = circle.getRadius();
		currenty = -(int) (radius / MapCircle.Y_SCALE);
		currentLineHalfWidth = circle.getHalfLineWidth(currenty);
		currentx = -currentLineHalfWidth;
	}

	@Override
	public final boolean hasNext() {
		return currenty < radius / MapCircle.Y_SCALE && currentx != Float.NaN;
	}

	/**
	 * NOTE: nextX() MUST BE CALLED after this call to progress to the next position.
	 * 
	 * @return gives the x of the current iterator position
	 */
	public final int nextY() {
		return currenty;
	}

	/**
	 * NOTE: nextY() MUST BE CALLED before this method is called!
	 * 
	 * @return gives the x of the current iterator position
	 */
	public final int nextX() {
		return computeNextXAndProgress();
	}

	@Override
	public ShortPoint2D next() {
		int y = currenty;
		int x = computeNextXAndProgress();

		return new ShortPoint2D(x, y);
	}

	private final int computeNextXAndProgress() {
		int x = (int) (.5f * currenty + currentx + 0.9999f);

		currentx++;
		if (currentx > currentLineHalfWidth) {
			// next line
			currenty++;
			currentLineHalfWidth = circle.getHalfLineWidth(currenty);
			currentx = -currentLineHalfWidth;
		}
		return x;
	}

	@Override
	public final void remove() {
		throw new UnsupportedOperationException("Cannot remove from a circle.");
	}
}