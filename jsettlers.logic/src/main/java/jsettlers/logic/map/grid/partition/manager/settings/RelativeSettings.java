/*
 * Copyright (c) 2017
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
package jsettlers.logic.map.grid.partition.manager.settings;

import java.io.Serializable;

import jsettlers.logic.constants.MatchConstants;

public class RelativeSettings<T extends Enum> implements Serializable {
	public interface OrdinalToTypeMapper<T extends Enum> extends Serializable {
		T map(int ordinal);
	}

	private final float[] userValues;
	private final OrdinalToTypeMapper<T> ordinalToTypeMapper;
	private final boolean decreaseOnDraw;
	private final float lowerBound;
	private final float upperBound;

	private float sum = 0;

	public RelativeSettings(int numberOfElements, OrdinalToTypeMapper<T> ordinalToTypeMapper, boolean decreaseOnDraw, float lowerBound, float upperBound) {
		this.userValues = new float[numberOfElements];
		this.ordinalToTypeMapper = ordinalToTypeMapper;
		this.decreaseOnDraw = decreaseOnDraw;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public RelativeSettings(int numberOfElements, OrdinalToTypeMapper<T> ordinalToTypeMapper, boolean decreaseOnDraw) {
		this(numberOfElements, ordinalToTypeMapper, decreaseOnDraw, 0, Float.MAX_VALUE);
	}

	public void setUserValue(T type, float value) {
		setUserValue(type.ordinal(), value);
	}

	private void setUserValue(int index, float value) {
		sum -= userValues[index];
		userValues[index] = Math.min(upperBound, Math.max(lowerBound, value));
		sum += userValues[index];
	}

	public void changeUserValue(T type, float delta) {
		changeUserValue(type.ordinal(), delta);
	}

	private void changeUserValue(int index, float delta) {
		setUserValue(index, userValues[index] + delta);
	}

	public float getUserValue(T type) {
		return userValues[type.ordinal()];
	}

	public float getProbability(T type) {
		return getProbability(type.ordinal());
	}

	private float getProbability(int index) {
		if (sum == 0) {
			return 0;
		}
		return userValues[index] / sum;
	}

	public T drawRandom() {
		float random = MatchConstants.random().nextFloat() * sum;
		if (sum == 0) { // if sum is 0, no elements are in the list => directly return
			return null;
		}

		float prefixSum = 0;
		for (int index = 0; index < userValues.length; index++) {
			prefixSum += userValues[index];
			if (random < prefixSum) {
				if (decreaseOnDraw) {
					changeUserValue(index, -1);
				}

				return ordinalToTypeMapper.map(index);
			}
		}
		return null;
	}
}
