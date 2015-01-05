package go.graphics.region;

import go.graphics.UIPoint;

/**
 * This is the position of a region inside an area.
 * 
 * @author michael
 */
public class PositionedRegion {
	private final int top;
	private final int bottom;
	private final int left;
	private final int right;
	private final Region region;

	public PositionedRegion(Region region, int top, int bottom, int left,
			int right) {
		this.region = region;
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;

	}

	public int getTop() {
		return top;
	}

	public int getBottom() {
		return bottom;
	}

	public int getLeft() {
		return left;
	}

	public int getRight() {
		return right;
	}

	public Region getRegion() {
		return region;
	}

	public boolean contentContains(UIPoint point) {
		return getLeft() <= point.getX() && getRight() > point.getX()
				&& getTop() > point.getY() && getBottom() <= point.getY();
	}
}
