package jsettlers.graphics.map;

import go.graphics.UIPoint;

import java.util.Hashtable;

import jsettlers.common.position.FloatRectangle;

public class ScreenPosition {

	private static final int TOPBORDER = 100;

	private FloatRectangle screen = new FloatRectangle(0, 0, 1, 1);

	/**
	 * The x coordinate of the current screen, without extra panning.
	 */
	private float screenCenterX;
	private float screenCenterY;

	private Hashtable<Object, UIPoint> panProgresses =
	        new Hashtable<Object, UIPoint>();

	private int mapWidth;

	private int mapHeight;

	private final float incline;

	/**
	 * Sets the map size, the max border, widthout the automatically added
	 * additional border.
	 * 
	 * @param mapWidth
	 * @param mapHeight
	 *            The width in pixel
	 * @param incline
	 *            The incline of the parallelogram side.
	 */
	public ScreenPosition(int mapWidth, int mapHeight, float incline) {
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.incline = incline;
	}

	private static float clamp(float min, float max, float value) {
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
	 * @param newWidth
	 *            The width.
	 * @param newHeight
	 *            The height.
	 * @param zoom 
	 */
	public void setSize(float newWidth, float newHeight)  {
		float x = this.screen.getCenterX();
		float y = this.screen.getCenterY();
		setScreen(x, y, newWidth, newHeight);
	}

	/**
	 * Sets the center of the screen.
	 * 
	 * @param x
	 *            X in pixels.
	 * @param y
	 *            Y in pixels.
	 */
	public void setScreenCenter(float x, float y) {
		this.screenCenterX = x;
		this.screenCenterY = y;
		recalculateScreen();
	}

	/**
	 * Recalculates the x and y position of the screen by the current pan
	 * values.
	 */
	private void recalculateScreen() {
		float x = this.screenCenterX;
		float y = this.screenCenterY;

		int xoffset = 0;
		int yoffset = 0;
		for (UIPoint p : this.panProgresses.values()) {
			xoffset += p.getX();
			yoffset += p.getY();
		}
		setScreen(x - xoffset, y - yoffset, this.screen.getWidth(),
		        this.screen.getHeight());

		this.screenCenterX = this.screen.getCenterX() + xoffset;
		this.screenCenterY = this.screen.getCenterY() + yoffset;
	}

	/**
	 * Sets the screen, and clamps it.
	 * 
	 * @param centerx
	 * @param centery
	 * @param newWidth
	 * @param newHeight
	 */
	private void setScreen(float centerx, float centery, float newWidth,
	        float newHeight) {
		// clamp to top and bottom

		// top = height in px.
		int top = this.mapHeight + TOPBORDER;
		int bottom = 0;

		float newCenterY =
		        clamp(bottom + newHeight / 2, top - newHeight / 2, centery);
		float miny = newCenterY - newHeight / 2;
		float maxy = miny + newHeight;

		// calculate left/right according to current y pos.
		int left = (int) (this.incline * miny);
		int right = (int) (this.incline * maxy) + this.mapWidth;

		float newCenterX =
		        clamp(left + newWidth / 2, right - newWidth / 2, centerx);
		float minx = newCenterX - newWidth / 2;
		float maxx = minx + newWidth;

		this.screen = new FloatRectangle(minx, miny, maxx, maxy);
	}

	public float getBottom() {
		return this.screen.getMinY();
	}

	public float getTop() {
		return this.screen.getMaxY();
	}

	public float getLeft() {
		return this.screen.getMinX();
	}

	public float getRight() {
		return this.screen.getMaxX();
	}

	public float getWidth() {
		return this.screen.getWidth();
	}

	public float getHeight() {
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
	public void setPanProgress(Object key, UIPoint distance) {
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
	public void finishPanProgress(Object key, UIPoint distance) {
		this.panProgresses.remove(key);
		setScreenCenter((int) (this.screenCenterX - distance.getX()),
		        (int) (this.screenCenterY - distance.getY()));
	}

	public FloatRectangle getPosition() {
		return this.screen;
	}

}
