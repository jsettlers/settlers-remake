/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.common.menu;

import java.io.Serializable;

import jsettlers.common.position.ShortPoint2D;

/**
 * This class contains the data needed for the GUI to restore it's state.
 * <p>
 * It will be serialized with the map on map saves.
 * 
 * @author Michael Zangl
 */
public class UIState implements Serializable {
	private static final long serialVersionUID = 4163727213374601975L;

	private final float screenCenterX;
	private final float screenCenterY;
	private final float zoom;
	private ShortPoint2D startPoint = null;

	/**
	 * Creates a {@link UIState} object for the given pixel position.
	 * 
	 * @param screenCenterX
	 *            Screen center
	 * @param screenCenterY
	 *            Screen center
	 * @param zoom
	 *            The zoom factor
	 */
	public UIState(float screenCenterX, float screenCenterY, float zoom) {
		this.screenCenterX = screenCenterX;
		this.screenCenterY = screenCenterY;
		this.zoom = zoom;
	}

	/**
	 * Creates a {@link UIState} for the given starting point.
	 * 
	 * @param startPoint
	 *            The point to center on
	 */
	public UIState(ShortPoint2D startPoint) {
		this(0, 0, 0);
		this.startPoint = startPoint;
	}

	/**
	 * Gets the pixel center of the screen.
	 * 
	 * @return The screen center x.
	 */
	public float getScreenCenterX() {
		return screenCenterX;
	}

	/**
	 * Gets the pixel center of the screen.
	 * 
	 * @return The screen center y.
	 */
	public float getScreenCenterY() {
		return screenCenterY;
	}

	/**
	 * Gets the zoom factor to use. Might be 0 to indicate default zoom.
	 * 
	 * @return The zoom.
	 */
	public float getZoom() {
		return zoom;
	}

	/**
	 * Gets the start point. This point overrides screen center settings.
	 * 
	 * @return the point.
	 */
	public ShortPoint2D getStartPoint() {
		return startPoint;
	}
}
