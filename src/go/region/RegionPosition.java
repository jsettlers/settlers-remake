package go.region;

import go.area.Area;

import java.awt.Rectangle;

public class RegionPosition {
	private final int top;
	private final int bottom;
	private final int left;
	private final int right;
	private final Region region;

	public RegionPosition(Region region, int top, int bottom, int left, int right) {
		this.region = region;
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;

	}

	public int getTop() {
		return this.top;
	}

	public int getBottom() {
		return this.bottom;
	}

	public int getLeft() {
		return this.left;
	}

	public int getRight() {
		return this.right;
	}

	public Rectangle getContent() {
		switch (getRegion().getPosition()) {
		case Region.POSITION_TOP:
			return new Rectangle(this.left, this.bottom + Area.BORDER_SIZE, this.right - this.left,
					this.top - this.bottom - Area.BORDER_SIZE);

		case Region.POSITION_BOTTOM:
			return new Rectangle(this.left, this.bottom, this.right - this.left, this.top - this.bottom
					- Area.BORDER_SIZE);

		case Region.POSITION_LEFT:
			return new Rectangle(this.left, this.bottom, this.right - this.left - Area.BORDER_SIZE,
					this.top - this.bottom);

		case Region.POSITION_RIGHT:
			return new Rectangle(this.left + Area.BORDER_SIZE, this.bottom, this.right - this.left
					- Area.BORDER_SIZE, this.top - this.bottom);

		default:
			return new Rectangle(this.left, this.bottom, this.right - this.left, this.top - this.bottom);
		}
	}

	public Rectangle getBorder() {
		switch (getRegion().getPosition()) {
		case Region.POSITION_TOP:
			return new Rectangle(this.left, this.bottom, this.right - this.left, Area.BORDER_SIZE);

		case Region.POSITION_BOTTOM:
			return new Rectangle(this.left, this.top - Area.BORDER_SIZE, this.right - this.left,
					Area.BORDER_SIZE);

		case Region.POSITION_RIGHT:
			return new Rectangle(this.left, this.bottom, Area.BORDER_SIZE, this.top - this.bottom);

		case Region.POSITION_LEFT:
			return new Rectangle(this.right - Area.BORDER_SIZE, this.bottom,
					Area.BORDER_SIZE, this.top - this.bottom);

		default:
			return null;
		}
	}

	public Region getRegion() {
		return this.region;
	}
}
