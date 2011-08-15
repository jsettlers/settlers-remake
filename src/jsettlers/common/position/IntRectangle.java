package jsettlers.common.position;

/**
 * This is an int rectangle. It covers the area from (including) x1 to
 * (unincluding) x2.
 * 
 * @author michael
 */
public class IntRectangle {
	private final int minx, miny, maxx, maxy;

	public IntRectangle(int minx, int miny, int maxx, int maxy) {
		this.minx = minx;
		this.miny = miny;
		this.maxx = maxx;
		this.maxy = maxy;
	}

	@Deprecated
	public int getX1() {
		return minx;
	}

	@Deprecated
	public int getY1() {
		return miny;
	}

	@Deprecated
	public int getX2() {
		return maxx;
	}

	@Deprecated
	public int getY2() {
		return maxy;
	}

	/**
	 * Gets the minimal x coordinate for a point that is inside the rect.
	 * 
	 * @return The x coordinate.
	 */
	public int getMinX() {
		return minx;
	}

	/**
	 * Gets the minimal y coordinate for a point that is inside the rect.
	 * 
	 * @return The y coordinate.
	 */
	public int getMinY() {
		return miny;
	}

	/**
	 * Gets the x coordinate for a point that is just outside of rect. Points
	 * with a x coordinate smaller than this value are inside the rect.
	 * 
	 * @return The x coordinate.
	 */
	public int getMaxX() {
		return maxx;
	}

	/**
	 * Gets the x coordinate for a point that is just outside of rect. Points
	 * with a x coordinate smaller than this value are inside the rect.
	 * 
	 * @return The x coordinate.
	 */
	public int getMaxY() {
		return maxy;
	}

	/**
	 * Gets the width of the rectangle. That states how many pixels are
	 * contained in it in x direction.
	 * 
	 * @return The width
	 */
	public int getWidth() {
		return maxx - minx;
	}

	/**
	 * Gets the height of the rectangle. That states how many pixels are
	 * contained in it in y direction.
	 * 
	 * @return The height
	 */
	public int getHeight() {
		return maxy - miny;
	}

	/**
	 * Gets the center of the rectangle in x direction.
	 * 
	 * @return The center.
	 */
	public int getCenterX() {
		return (minx + maxx) / 2;
	}

	/**
	 * Gets the center of the rectangle in y direction.
	 * 
	 * @return The center.
	 */
	public int getCenterY() {
		return (miny + maxy) / 2;
	}

	/**
	 * Checks whether a point is inside the rectangle.
	 * 
	 * @param x
	 *            The x coordinate to check
	 * @param y
	 *            The y coordinate to check
	 * @return If the point is inside.
	 */
	public boolean contains(int x, int y) {
		return x >= minx && x < maxx && y >= miny && y < maxy;
	}

	/**
	 * Shrinks the rectangle by the given amount. The center of the new
	 * rectangle is the same as the one of the old one, but its size is in each
	 * direction 2*border bigger.
	 * 
	 * @param border
	 *            the size to reduce the rectangle on each side. If it is
	 *            negative, the rectangle is made smaller.
	 * @return The bigger rectangle.
	 */
	public IntRectangle bigger(int border) {
		return new IntRectangle(minx - border, miny - border, maxx + border,
		        maxy + border);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntRectangle) {
			return equals((IntRectangle) obj);
		} else {
			return false;
		}
	}

	private boolean equals(IntRectangle rect) {
		return rect.minx == minx && rect.miny == miny && rect.maxx == maxx
		        && rect.maxy == maxy;
	}
	
	@Override
	public String toString() {
	    return "rect[minx=" + minx + ",miny=" + miny +",maxx="+ maxx +",maxy="+ maxy + "]";
	}

	@Override
	public int hashCode() {
		return minx * 104729 + miny * 4900099 + maxx * 135084239 + maxy;
	}
}