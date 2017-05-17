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
package go.graphics.event.interpreter;

import go.graphics.UIPoint;
import go.graphics.event.mouse.GOZoomEvent;

/**
 * Event to zoom in / out
 * 
 * @author Andreas Butti
 */
public class ConvertedZoomEvent extends AbstractMouseEvent implements GOZoomEvent {

	/**
	 * A float. 1 means no zoom, small values mean smaller, big values mean bigger.
	 */
	private float zoom;
	private UIPoint pointingPosition;

	/**
	 * Constructor
	 */
	public ConvertedZoomEvent() {
	}

	@Override
	public float getZoomFactor() {
		return zoom;
	}

	public UIPoint getPointingPosition() {
		return pointingPosition;
	}

	/**
	 * Sets the zoom factor
	 * 
	 * @param factor
	 *            A float. 1 means no zoom, small values mean smaller, big values mean bigger.
	 */
	public void setZoomFactor(float factor, UIPoint pointingPosition) {
		this.zoom = factor;
		this.pointingPosition = pointingPosition;
		fireModalDataRefreshed();
	}

}
