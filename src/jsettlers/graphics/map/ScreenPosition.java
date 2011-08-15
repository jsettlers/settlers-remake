package jsettlers.graphics.map;

import java.awt.Point;
import java.util.Hashtable;

import jsettlers.common.position.IntRectangle;

public class ScreenPosition {

	private static final int TOPBORDER = 100;

	private IntRectangle screen = new IntRectangle(0, 0, 1, 1);

	/**
	 * The x coordinate of the current screen, without extra panning.
	 */
	private int screenCenterX;
	private int screenCenterY;

	private Hashtable<Object, Point> panProgresses =
	        new Hashtable<Object, Point>();

	private int mapWidth;

	private int mapHeight;

	private final float incline;

	/**
	 * Sets the map size, the max border, widthout the automatically added
	 * additional border.
	 * 
	 * @param mapWidth
	 * @param mapHeight The width in pixel
	 * @param incline The incline of the parallelogram side.
	 */
	public ScreenPosition(int mapWidth, int mapHeight, float incline) {
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.incline = incline;
	}

	private int clamp(int min, int max, int value) {
		if (min > max) {
			return (min + max) / 2;
		} else if (value < min) {
			return min;
		} else if (value > max) {
			return max;
		} else {
			return value;
		}
	}

	/**
	 * Sets the size of the context to width/height.
	 * 
	 * @param width
	 *            The width.
	 * @param height
	 *            The height.
	 */
	public void setSize(int width, int height) {
		int x = this.screen.getCenterX();
		int y = this.screen.getCenterY();
		setScreen(x, y, width, height);
	}

	/**
	 * Sets the center of the screen.
	 * 
	 * @param x
	 *            X in pixels.
	 * @param y
	 *            Y in pixels.
	 */
	public void setScreenCenter(int x, int y) {
		this.screenCenterX = x;
		this.screenCenterY = y;
		recalculateScreen();
	}

	/**
	 * Recalculates the x and y position of the screen.
	 */
	private void recalculateScreen() {
		int x = this.screenCenterX;
		int y = this.screenCenterY;

		int xoffset = 0;
		int yoffset = 0;
		for (Point p : this.panProgresses.values()) {
			xoffset += p.x;
			yoffset += p.y;
		}
		setScreen(x - xoffset, y - yoffset, this.screen.getWidth(), this.screen
		        .getHeight());

		this.screenCenterX = this.screen.getCenterX() + xoffset;
		this.screenCenterY = this.screen.getCenterY() + yoffset;
	}

	/**
	 * Sets the screen, and clamps it.
	 * 
	 * @param centerx
	 * @param centery
	 * @param width
	 * @param height
	 */
	private void setScreen(int centerx, int centery, int width, int height) {
		// clamp to top and bottom

		// top = height in px.
		int top = this.mapHeight + TOPBORDER;
		int bottom = 0;

		int newCenterY = clamp(bottom + height / 2, top - height / 2, centery);
		int miny = newCenterY - height / 2;
		int maxy = miny + height;

		// calculate left/right according to current y pos.
		int left = (int) (this.incline * miny);
		int right =
		        (int) (this.incline * maxy) + this.mapWidth;

		int newCenterX = clamp(left + width / 2, right - width / 2, centerx);
		int minx = newCenterX - width / 2;
		int maxx = minx + width;

		this.screen = new IntRectangle(minx, miny, maxx, maxy);
	}

	public int getBottom() {
		return this.screen.getY1();
	}

	public int getTop() {
		return this.screen.getY2();
	}

	public int getLeft() {
		return this.screen.getX1();
	}

	public int getRight() {
		return this.screen.getX2();
	}

	public int getWidth() {
		return this.screen.getWidth();
	}

	public int getHeight() {
		return this.screen.getHeight();
	}

	/**
	 * Sets the temporary pan progress for a given pan operation.
	 * 
	 * @param key
	 *            The identifier of the event
	 * @param distance
	 *            The distance we panned.
	 */
	public void setPanProgress(Object key, Point distance) {
		this.panProgresses.put(key, distance);
		recalculateScreen();
	}

	/**
	 * Sets the temporary pan progress for a given pan operation.
	 * 
	 * @param key
	 *            The identifier of the event
	 * @param distance
	 *            The actual distance when the event ended.
	 */
	public void finishPanProgress(Object key, Point distance) {
		this.panProgresses.remove(key);
		setScreenCenter(this.screenCenterX - distance.x, this.screenCenterY - distance.y);
	}

	public IntRectangle getPosition() {
	    return this.screen;
    }

}
