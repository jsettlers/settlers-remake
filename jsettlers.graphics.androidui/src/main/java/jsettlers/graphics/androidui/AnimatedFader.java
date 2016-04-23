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

/**
 * This class manages fading.
 * <p>
 * You can just make something fade to a given value, or you can force it to values (e.g. for manual scrolling).
 * 
 * @author michael
 */
public class AnimatedFader {
	private static final int ANIMATION_TIME = 500;
	private final float min;
	private final float max;
	/**
	 * The value where animation started.
	 */
	private float startValue;
	private float desired;
	private long startTime;

	public AnimatedFader(float min, float max) {
		this.min = min;
		this.max = max;
		this.startValue = min;
	}

	/**
	 * Sets the fader to a given value. Does not change it.
	 * 
	 * @param value
	 *            The value.
	 */
	public void setTo(float value) {
		fadeTo(value);
		this.startValue = this.desired;
	}

	public void fadeTo(float value) {
		this.startValue = this.desired;
		if (value < min) {
			this.desired = min;
		} else if (value > max) {
			this.desired = max;
		} else {
			this.desired = value;
		}
		this.startTime = System.currentTimeMillis();
	}

	public float getValue() {
		long progress = System.currentTimeMillis() - startTime;
		if (progress > 500) {
			return desired;
		} else {
			float a =
					(float) (Math.cos((double) progress / ANIMATION_TIME
							* Math.PI) / 2 + .5);
			return startValue * a + desired * (1 - a);
		}
	}
}
