package go.graphics.region;

import go.graphics.area.Area;

import java.awt.Rectangle;

/**
 * This is the position of a region inside an area.
 * @author michael
 *
 */
public class PositionedRegion {
	private final int top;
	private final int bottom;
	private final int left;
	private final int right;
	private final Region region;

	public PositionedRegion(Region region, int top, int bottom, int left, int right) {
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

	public Rectangle getContent() {
		switch (getRegion().getPosition()) {
		case Region.POSITION_TOP:
			return new Rectangle(left, bottom + Area.BORDER_SIZE, right - left,
					top - bottom - Area.BORDER_SIZE);

		case Region.POSITION_BOTTOM:
			return new Rectangle(left, bottom, right - left, top - bottom
					- Area.BORDER_SIZE);

		case Region.POSITION_LEFT:
			return new Rectangle(left, bottom, right - left - Area.BORDER_SIZE,
					top - bottom);

		case Region.POSITION_RIGHT:
			return new Rectangle(left + Area.BORDER_SIZE, bottom, right - left
					- Area.BORDER_SIZE, top - bottom);

		default:
			return new Rectangle(left, bottom, right - left, top - bottom);
		}
	}

	public Rectangle getBorder() {
		switch (getRegion().getPosition()) {
		case Region.POSITION_TOP:
			return new Rectangle(left, bottom, right - left, Area.BORDER_SIZE);

		case Region.POSITION_BOTTOM:
			return new Rectangle(left, top - Area.BORDER_SIZE, right - left,
					Area.BORDER_SIZE);

		case Region.POSITION_RIGHT:
			return new Rectangle(left, bottom, Area.BORDER_SIZE, top - bottom);

		case Region.POSITION_LEFT:
			return new Rectangle(right - Area.BORDER_SIZE, bottom,
					Area.BORDER_SIZE, top - bottom);

		default:
			return null;
		}
	}

	public Region getRegion() {
		return region;
	}
}
