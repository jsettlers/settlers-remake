package jsettlers.common.map.shapes;

import java.util.Iterator;
import java.util.NoSuchElementException;

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
	public boolean hasNext() {
		return currenty < radius / MapCircle.Y_SCALE && currentx != Float.NaN;
	}

	@Override
	public ISPosition2D next() {
		assert radius / MapCircle.Y_SCALE >= Math.abs(currenty);
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		// TODO: how to use that .5f*currenty?
		int x = (int) Math.ceil(.5f * currenty + currentx) + centerx;
		int y = currenty + centery;

		currentx++;
		if (currentx > currentLineHalfWidth) {
			// next line
			currenty++;
			currentLineHalfWidth = circle.getHalfLineWidth(currenty);
			currentx = -currentLineHalfWidth;
		}

		return new ShortPoint2D(x, y);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove from a circle.");
	}
}