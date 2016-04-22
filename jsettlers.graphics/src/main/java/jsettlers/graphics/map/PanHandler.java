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
package jsettlers.graphics.map;

import go.graphics.UIPoint;
import go.graphics.event.GOEvent;
import go.graphics.event.GOModalEventHandler;
import go.graphics.event.mouse.GOPanEvent;

/**
 * This is a pan handler. It just saves the pan status for polling.
 * <p>
 * Pan handlers may only be added to pan events.
 * 
 * @author michael
 */
public class PanHandler implements GOModalEventHandler {

	private final ScreenPosition context;

	/**
	 * Creates a new pan handler.
	 * 
	 * @param context
	 *            The screen position this handler should be apply to.
	 */
	public PanHandler(ScreenPosition context) {
		this.context = context;
		context.setPanProgress(this, new UIPoint(0, 0));
	}

	@Override
	public void aborted(GOEvent event) {
		this.context.finishPanProgress(this, new UIPoint(0, 0));
	}

	@Override
	public void finished(GOEvent event) {
		this.context.finishPanProgress(this, ((GOPanEvent) event)
				.getPanDistance());
	}

	@Override
	public void phaseChanged(GOEvent event) {

	}

	@Override
	public void eventDataChanged(GOEvent event) {
		this.context
				.setPanProgress(this, ((GOPanEvent) event).getPanDistance());
	}
}
