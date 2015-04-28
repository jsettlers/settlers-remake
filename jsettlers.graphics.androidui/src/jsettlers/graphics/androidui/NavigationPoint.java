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
package jsettlers.graphics.androidui;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;

import java.util.Timer;
import java.util.TimerTask;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.controls.original.panel.content.AnimateablePosition;
import jsettlers.graphics.map.draw.ImageProvider;

public class NavigationPoint {
	private static final int CENTER_SIZE = 40;
	private static final int CLICK_AREA_SIZE = 80;
	private static final long PAN_TIMER_PERIOD = 50;
	private float x;
	private float y;
	private float fullSize;
	private boolean panInProgress;
	private AnimateablePosition panPosition = new AnimateablePosition(0, 0);

	private final Object panTimerMutex = new Object();
	private Timer panTimer;
	private TimerTask panTimerTask;
	private MapDrawContext context;

	public NavigationPoint(MapDrawContext context) {
		this.context = context;
	}

	public void setPosition(float x, float y, float fullSize) {
		this.x = x;
		this.y = y;
		this.fullSize = fullSize;
		panPosition = new AnimateablePosition(x, y);
	}

	public void drawAt(GLDrawContext gl) {
		ImageProvider
				.getInstance()
				.getImage(
						new OriginalImageLink(EImageLinkType.SETTLER, 4, 8, 2))
				.drawAt(gl, panPosition.getX(), panPosition.getY());
	}

	public boolean centerContains(UIPoint drawPosition) {
		return distanceToCenter(drawPosition) < CLICK_AREA_SIZE / 2;
	}

	private double distanceToCenter(UIPoint drawPosition) {
		return Math.hypot(drawPosition.getX() - x, drawPosition.getY() - y);
	}

	public void setPanningPosition(UIPoint drawPosition) {
		panInProgress = true;
		double x = drawPosition.getX();
		double y = drawPosition.getY();
		if (distanceToCenter(drawPosition) > fullSize / 2) {
			double scale = fullSize / 2 / distanceToCenter(drawPosition);
			x = (1 - scale) * this.x + scale * x;
			y = (1 - scale) * this.y + scale * y;
		}
		panPosition.setPosition((float) x, (float) y);

		startPanTimer();
	}

	private void startPanTimer() {
		synchronized (panTimerMutex) {
			if (panTimer == null) {
				panTimer = new Timer("pan timer");
			}
			if (panTimerTask == null) {
				panTimerTask = new TimerTask() {
					@Override
					public void run() {
						panTimerTick();
					}
				};
				panTimer.schedule(panTimerTask, PAN_TIMER_PERIOD,
						PAN_TIMER_PERIOD);
			}
		}
	}

	/**
	 * The rest we store for the next time
	 */
	private double panDx, panDy;

	protected void panTimerTick() {
		synchronized (panTimerMutex) {
			if (!panInProgress && panPosition.getX() == x
					&& panPosition.getY() == y) {
				panTimerTask.cancel();
				panTimerTask = null;
			}
			panDx -= panPosition.getX() - x;
			panDy -= panPosition.getY() - y;
		}

		if (context != null) {
			System.out.println("scroll: " + panDx + ", " + panDy);
			context.getScreen().setPanProgress(this, new UIPoint(panDx, panDy));
		}

	}

	public void abortPanning() {
		panInProgress = false;
		panPosition.setPosition(x, y);
		if (context != null) {
			context.getScreen().finishPanProgress(this,
					new UIPoint(panDx, panDy));
		}
		panDx = 0;
		panDy = 0;
	}

	public boolean isPanInProgress() {
		return panInProgress;
	}
}
