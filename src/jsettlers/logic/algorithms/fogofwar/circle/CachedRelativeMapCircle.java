package jsettlers.logic.algorithms.fogofwar.circle;

import jsettlers.common.map.shapes.MapCircle;

/**
 * 
 * 
 * @author Andreas Eberle
 * 
 */
public final class CachedRelativeMapCircle {

	private final float radius;
	final float[] cachedHalfLineWidths;
	private int cacheOffset;

	public CachedRelativeMapCircle(float radius) {
		this.radius = radius;

		int diameter = (int) (2 * radius / MapCircle.Y_SCALE) + 3;
		cacheOffset = (int) (radius / MapCircle.Y_SCALE) + 1;
		cachedHalfLineWidths = new float[diameter];

		for (int y = -cacheOffset; y < cacheOffset; y++) {
			cachedHalfLineWidths[y + cacheOffset] = calcHalfLineWidth(y, radius);
		}
	}

	public CachedRelativeCircleIterator iterator() {
		return new CachedRelativeCircleIterator(this);
	}

	/**
	 * Gets the half width of a line, rounded.
	 * 
	 * @param relativey
	 *            The x coordinate of the line relative to the center
	 * @return The width of the line, NAN if the line is outside the circle.
	 */
	private static final float calcHalfLineWidth(int relativey, float radius) {
		double maximum = Math.sqrt(radius * radius - relativey * MapCircle.Y_SCALE * relativey * MapCircle.Y_SCALE);
		if (relativey % 2 == 0) {
			// round to tiles.
			return (float) Math.floor(maximum);
		} else {
			// uneven line, round x to *.5
			return (float) (Math.floor(maximum + .5) - .5);
		}
	}

	final float getRadius() {
		return radius;
	}

	final float getHalfLineWidth(int relativeY) {
		return cachedHalfLineWidths[relativeY + cacheOffset];
	}
}
