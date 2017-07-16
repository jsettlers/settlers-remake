/*******************************************************************************
 * Copyright (c) 2015 - 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.graphics.map;

import java.util.Hashtable;

import go.graphics.UIPoint;
import jsettlers.common.position.FloatRectangle;

/**
 * This class defines and handles the position of the map view.
 * 
 * @author Michael Zangl
 */
public class ScreenPosition {

	/**
	 * Minimum zoom allowed
	 */
	private static final float MINIMUM_ZOOM = .2f;

	/**
	 * Maximum zoom allowed
	 */
	private static final float MAXIMUM_ZOOM = 3f;

	private static final int TOPBORDER = 100;

	private FloatRectangle screen = new FloatRectangle(0, 0, 1, 1);

	/**
	 * zoom factor. The smaller the smaller the settlers get.
	 */
	private float zoom = 1;
	private float oldZoom = 1;

	/**
	 * the pointing position when zooming
	 */
	private UIPoint pointer = null;

	/**
	 * The x coordinate of the current screen, without extra panning.
	 */
	private float screenCenterX;
	private float screenCenterY;

	private final Hashtable<Object, UIPoint> panProgresses = new Hashtable<>();

	private final int mapWidth;

	private final int mapHeight;

	private final float incline;

	/**
	 * Sets the map size, the max border, without the automatically added additional border.
	 * 
	 * @param mapWidth
	 *            The map width in pixel on the screen.
	 * @param mapHeight
	 *            The map height in pixel.
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
	 * Sets the size of the screen without the zoom level applied.
	 * When called for zooming, screen at pointer stands still.
	 * 
	 * @param newWidth
	 *            The width.
	 * @param newHeight
	 *            The height.
	 */
	public void setSize(float newWidth, float newHeight) {
		if (newHeight / zoom > mapHeight + TOPBORDER) {
			zoom = newHeight / (mapHeight + TOPBORDER);
		}

		float x = screen.getCenterX();
		float y = screen.getCenterY();
		if (pointer != null) {
			float w = screen.getWidth();
			float h = screen.getHeight();
			float px = (float) pointer.getX();
			float py = (float) pointer.getY();
			x -= w / 2 - px / oldZoom + px / zoom - w / 2 * oldZoom / zoom;
			y -= h / 2 - py / oldZoom + py / zoom - h / 2 * oldZoom / zoom;
			screenCenterX = x;
			screenCenterY = y;
		}
		setScreen(x, y, newWidth / zoom, newHeight / zoom);
	}

	/**
	 * Set the new zoom factor.
	 * 
	 * @param newZoom
	 *            The new zoom factor. It is automatically clamped.
	 */
	public void setZoom(float newZoom, UIPoint pointingPosition) {
		oldZoom = zoom;
		zoom = clamp(MINIMUM_ZOOM, MAXIMUM_ZOOM, newZoom);
		pointer = pointingPosition;
	}

	/**
	 * Sets the center of the screen.
	 * 
	 * @param x
	 *            X in pixels.
	 * @param y
	 *            Y in pixels.
	 */
	public synchronized void setScreenCenter(float x, float y) {
		this.screenCenterX = x;
		this.screenCenterY = y;
		recalculateScreen();
	}

	/**
	 * Gets the current center of the screen.
	 * 
	 * @return The x coordinate of the center. This also includes any ongoing pan operations.
	 */
	public float getScreenCenterX() {
		return this.screen.getCenterX();
	}

	/**
	 * Gets the current center of the screen.
	 * 
	 * @return The y coordinate of the center. This also includes any ongoing pan operations.
	 */
	public float getScreenCenterY() {
		return screenCenterY;
	}

	/**
	 * Recalculates the x and y position of the screen by the current pan values.
	 */
	private void recalculateScreen() {
		float x = this.screenCenterX;
		float y = this.screenCenterY;

		int xoffset = 0;
		int yoffset = 0;
		for (UIPoint p : this.panProgresses.values()) {
			xoffset += p.getX() / zoom;
			yoffset += p.getY() / zoom;
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
	 *            Screen center, in pixel.
	 * @param centery
	 *            Screen center, in pixel.
	 * @param newWidth
	 *            Screen width in pixel
	 * @param newHeight
	 *            Screen height in pixel
	 */
	private void setScreen(float centerx, float centery, float newWidth,
			float newHeight) {
		// clamp to top and bottom

		// top = height in px.
		int top = this.mapHeight + TOPBORDER;
		int bottom = 0;

		float newCenterY = clamp(bottom + newHeight / 2, top - newHeight / 2, centery);
		float miny = newCenterY - newHeight / 2;
		float maxy = miny + newHeight;

		// calculate left/right according to current y pos.
		int left = (int) (this.incline * miny);
		int right = (int) (this.incline * maxy) + this.mapWidth;

		float newCenterX = clamp(left + newWidth / 2, right - newWidth / 2, centerx);
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

	public float getZoom() {
		return zoom;
	}

	/**
	 * Sets the temporary pan progress for a given pan operation.
	 * 
	 * @param key
	 *            The identifier of the event
	 * @param distance
	 *            The distance we panned.
	 */
	public synchronized void setPanProgress(Object key, UIPoint distance) {
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
	public synchronized void finishPanProgress(Object key, UIPoint distance) {
		this.panProgresses.remove(key);
		setScreenCenter((int) (this.screenCenterX - distance.getX() / zoom),
				(int) (this.screenCenterY - distance.getY() / zoom));
	}

	public FloatRectangle getPosition() {
		return this.screen;
	}

}
