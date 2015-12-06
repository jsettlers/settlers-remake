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
package jsettlers.logic.objects;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.grid.objects.AbstractObjectsManagerObject;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class ProgressingObject extends AbstractObjectsManagerObject {
	private static final long serialVersionUID = 62117014829414034L;

	private int startTime;
	private int duration;

	protected ProgressingObject(ShortPoint2D pos) {
		super(pos);
	}

	@Override
	public float getStateProgress() {
		float progress = (MatchConstants.clock().getTime() - startTime) / ((float) duration);
		if (progress < 1) {
			return progress;
		} else {
			return 1;
		}
	}

	/**
	 * 
	 * @param duration
	 *            in seconds<br>
	 *            NOTE: duration MAY NEVER BE LESS OR EQUAL TO 0.0f
	 */
	protected final void setDuration(float duration) {
		// assert duration > 0 : "duration may never be less or equal to 0.0f"; TODO @Andreas Eberle enable this assertion again!

		this.duration = (int) (duration * 1000);
		this.startTime = MatchConstants.clock().getTime();
	}

	/**
	 * 
	 * @param duration
	 *            in seconds<br>
	 *            NOTE: the duration can vary up to +- 5% <br>
	 *            NOTE: duration MAY NEVER BE 0.0f
	 */
	protected final void setDurationWithVariation(float duration) {
		this.duration = (int) (duration * 1000);
		this.startTime = MatchConstants.clock().getTime() - MatchConstants.random().nextInt((int) (duration * 100));
	}

	/**
	 * @return the time when this object's getStateProgress() method will return 1.0 (in seconds)
	 */
	public float getEndTime() {
		return ((float) duration) / 1000;
	}
}
