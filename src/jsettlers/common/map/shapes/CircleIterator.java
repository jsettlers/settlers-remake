package jsettlers.common.map.shapes;

import java.util.Iterator;

import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;

public class CircleIterator implements Iterator<ISPosition2D> {
	protected int currenty;

	protected float currentLineHalfWidth;
	// x from vertical center line of circle.
	protected float currentx;

	protected final double radius;

	protected final short centerx;

	protected final short centery;

	private final MapCircle circle;

	public CircleIterator(MapCircle circle) {
		this.circle = circle;
		radius = circle.getRadius();
		currenty = -(int) (radius / MapCircle.Y_SCALE);
		currentLineHalfWidth = circle.getHalfLineWidth(currenty);
		currentx = -currentLineHalfWidth;

		centerx = circle.getCenterX();
		centery = circle.getCenterY();
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
		return currenty + centery;
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
	public ISPosition2D next() {
		int y = currenty + centery;
		int x = computeNextXAndProgress();

		return new ShortPoint2D(x, y);
	}

	private final int computeNextXAndProgress() {
		int x = (int) Math.ceil(.5f * currenty + currentx) + centerx;

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
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove from a circle.");
	}
}