/*
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
 */
package jsettlers.graphics.map.controls.original.panel.content.material.priorities;

public class AnimatablePosition {
	private static final long ANIMATION_TIME = 300;
	private float startx, starty;
	private float destx, desty;
	private long animationstart = 0;

	public AnimatablePosition(float startx, float starty) {
		this.startx = startx;
		this.starty = starty;
		this.destx = startx;
		this.desty = starty;
	}

	public float getX() {
		float p = getProgress();
		return (1 - p) * startx + p * destx;
	}

	private float getProgress() {
		if (animationstart == 0) {
			return 1; // faster
		}

		long timediff = System.currentTimeMillis() - animationstart;
		if (timediff >= ANIMATION_TIME) {
			animationstart = 0;
			return 1;
		}
		return (float) timediff / ANIMATION_TIME;
	}

	public float getY() {
		float p = getProgress();
		return (1 - p) * starty + p * desty;
	}

	public void setPosition(float x, float y) {
		this.startx = getX();
		this.starty = getY();
		this.animationstart = System.currentTimeMillis();
		this.destx = x;
		this.desty = y;
	}
}
