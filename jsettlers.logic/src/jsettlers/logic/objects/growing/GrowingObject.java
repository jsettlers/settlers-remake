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
package jsettlers.logic.objects.growing;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.objects.ProgressingObject;

/**
 * This is an abstract class used for growing objects.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class GrowingObject extends ProgressingObject {
	private static final long serialVersionUID = -5886720986614326428L;

	private EMapObjectType state;

	protected GrowingObject(ShortPoint2D pos, EMapObjectType growing) {
		super(pos);

		this.state = growing;
		super.setDuration(getGrowthDuration());
	}

	protected abstract float getGrowthDuration();

	public boolean isDead() {
		return this.state == getDeadState();
	}

	protected abstract EMapObjectType getDeadState();

	public boolean isAdult() {
		return this.state == getAdultState();
	}

	protected abstract EMapObjectType getAdultState();

	@Override
	public boolean canBeCut() {
		return isAdult();
	}

	@Override
	public boolean cutOff() {
		super.setDuration(getDecomposeDuration());
		this.state = getDeadState();
		return true;
	}

	protected abstract float getDecomposeDuration();

	@Override
	public EMapObjectType getObjectType() {
		return this.state;
	}

	@Override
	protected void changeState() {
		if (state == getAdultState()) {
			state = getDeadState();
		} else if (state == getDeadState()) {
		} else {
			state = getAdultState();
		}
	}
}
