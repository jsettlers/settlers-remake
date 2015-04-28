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
package go.graphics.nativegl;

import go.graphics.UIPoint;
import go.graphics.event.GOEventHandlerProvider;
import go.graphics.event.interpreter.AbstractEventConverter;

public class NativeEventConverter extends AbstractEventConverter {
	private static final double CLICK_MOVE_TRESHOLD = 5;
	private static final double CLICK_TIME_TRSHOLD = 1;
	
	private int panButton = -1;
	private boolean mouseIsInside;
	private boolean requestStartHover;
	private UIPoint currentMousePosition = new UIPoint(0, 0);

	protected NativeEventConverter(GOEventHandlerProvider provider) {
		super(provider);
		addReplaceRule(new EventReplacementRule(ReplacableEvent.DRAW,
		        Replacement.COMMAND_SELECT, CLICK_TIME_TRSHOLD,
		        CLICK_MOVE_TRESHOLD));
		addReplaceRule(new EventReplacementRule(ReplacableEvent.PAN,
		        Replacement.COMMAND_ACTION, CLICK_TIME_TRSHOLD,
		        CLICK_MOVE_TRESHOLD));
	}

	public void mouseDown(int button, int x, int y) {
		if (button == 0) {
			startDraw(new UIPoint(x, y));
		} else {
			startPan(new UIPoint(x, y));
			panButton = button;
		}
	}

	public void mouseUp(int button, int x, int y) {
		if (button == 0) {
			endDraw(new UIPoint(x, y));
		} else {
			if (panButton == button) {
				endPan(new UIPoint(x, y));
			}
		}
	}

	public void mousePositionChanged(int x, int y) {
		currentMousePosition = new UIPoint(x, y);
		if (drawStarted()) {
			updateDrawPosition(currentMousePosition);
		}
		if (panStarted()) {
			updatePanPosition(currentMousePosition);
		}
		if (requestStartHover) {
			startHover(currentMousePosition);
			requestStartHover = false;
		}
		if (mouseIsInside) {
			updateHoverPosition(currentMousePosition);
		}
	}

	public void mouseInsideWindow(boolean inside) {
		mouseIsInside = inside;
		requestStartHover = inside;
		if (!inside) {
			endHover(currentMousePosition);
		}
	}

	public void keyReleased(String key) {
		endKeyEvent(key);
	}

	public void keyPressed(String key) {
		startKeyEvent(key);
	}

}
