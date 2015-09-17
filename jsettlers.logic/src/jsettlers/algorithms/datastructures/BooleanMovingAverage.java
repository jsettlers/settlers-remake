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
package jsettlers.algorithms.datastructures;

import java.io.Serializable;

/**
 * Class to calculate the moving average of a sequence of boolean values.
 * 
 * @author Andreas Eberle
 *
 */
public class BooleanMovingAverage implements Serializable {
	private static final long serialVersionUID = -8487849794368180355L;

	private final boolean[] values;
	private int count;
	private int index = 0;

	public BooleanMovingAverage(int numberOfValues, boolean defaultValue) {
		values = new boolean[numberOfValues];

		if (defaultValue) {
			for (int i = 0; i < numberOfValues; i++) {
				values[i] = defaultValue;
			}
			count = numberOfValues;
		}
	}

	public void inserValue(boolean value) {
		boolean oldValue = values[index];
		values[index] = value;
		index = (index + 1) % values.length;

		if (oldValue) {
			count--;
		}
		if (value) {
			count++;
		}
	}

	public float getAverage() {
		return ((float) count) / values.length;
	}
}
