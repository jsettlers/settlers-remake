package jsettlers.common.map.shapes;

import jsettlers.common.position.ISPosition2D;

/**
 * This class represents a circular area of the map.
 * <p>
 * It contains all elements whose distance to the center is smaller or equal to radius.
 * <p>
 * Geometry calculations (a is the constant {@link MapCircle#Y_SCALE}:
 * <p>
 * <img src="doc-files/MapCircle-1.png">
 * 
 * @author michael
 */
public final class MapCircle implements IMapArea {
	private static final long serialVersionUID = 1L;

	private final float radius;
	private final short cy;
	private final short cx;

	/**
	 * Factor so that d((0,0), (1,1)) is almost 1.
	 */
	public final static float Y_SCALE = (float) Math.sqrt(3) / 2.0f * .999999f;

	public MapCircle(ISPosition2D pos, float radius) {
		this(pos.getX(), pos.getY(), radius);
	}

	public MapCircle(int cx, int cy, float radius) {
		this.cx = (short) cx;
		this.cy = (short) cy;
		this.radius = radius;
	}

	@Override
	public final boolean contains(ISPosition2D position) {
		return contains(position.getX(), position.getY());
	}

	public final boolean contains(int x, int y) {
		float distance = squaredDistanceToCenter(x, y);
		return distance <= radius * radius;
	}

	@Override
	public final MapCircleIterator iterator() {
		return new MapCircleIterator(this);
	}

	/**
	 * Gets the distance of map coordinates to the center.
	 * 
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate
	 * @return The distance to the center of this circle, so that the tiles around the center all have distance 1.
	 */
	public final float squaredDistanceToCenter(int x, int y) {
		int dx = x - cx;
		int dy = y - cy;
		return getSquaredDistance(dx, dy);
	}

	/**
	 * Gives the squared distance for given delta x and delta y
	 * 
	 * @param dx
	 * @param dy
	 * @return
	 */
	public static float getSquaredDistance(int dx, int dy) {
		return (.25f + Y_SCALE * Y_SCALE) * dy * dy + dx * dx - dx * dy;
	}

	public static final float getDistance(int x1, int y1, int x2, int y2) {
		float squared = getDistanceSquared(x1, y1, x2, y2);
		return (float) Math.sqrt(squared);
	}

	public static final float getDistanceSquared(ISPosition2D pos1, ISPosition2D pos2) {
		return getDistanceSquared(pos1.getX(), pos1.getY(), pos2.getX(), pos2.getY());
	}

	public static final float getDistanceSquared(int x1, int y1, int x2, int y2) {
		int dx = x1 - x2;
		int dy = y1 - y2;
		return getSquaredDistance(dx, dy);
	}

	/**
	 * Gets the half width of a line, roundend.
	 * 
	 * @param relativey
	 *            The x coordinate of the line relative to the center
	 * @return The width of the line, NAN if the line is outside the circle.
	 */
	protected final float getHalfLineWidth(int relativey) {
		double maximum = Math.sqrt(radius * radius - relativey * MapCircle.Y_SCALE * relativey * MapCircle.Y_SCALE);
		if (relativey % 2 == 0) {
			// round to tiles.
			return (float) Math.floor(maximum);
		} else {
			// uneven line, round x to *.5
			return (float) (Math.floor(maximum + .5) - .5);
		}
	}

	public final double distanceToCenter(int x, int y) {
		return Math.sqrt(squaredDistanceToCenter(x, y));
	}

	public final boolean isCloserToCenter(int x, int y, int minradius) {
		return squaredDistanceToCenter(x, y) < minradius * minradius;
	}

	public final float getRadius() {
		return radius;
	}

	public final short getCenterY() {
		return cy;
	}

	public final short getCenterX() {
		return cx;
	}
}
