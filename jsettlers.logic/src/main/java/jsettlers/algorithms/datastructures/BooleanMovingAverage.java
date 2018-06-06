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
import java.util.Arrays;

/**
 * Class to calculate the moving average of a sequence of boolean values.
 * 
 * @author Andreas Eberle
 *
 */
public class BooleanMovingAverage implements Serializable {
	private static final long serialVersionUID = -8487849794368180355L;

	private final boolean[] values;
	private int countTrue;
	private int index = 0;

	/**
	 * Creates a fix sized boolean array filled with the default value.
	 * @param numberOfValues the length of the array
	 * @param defaultValue the default value to fill all entries of the array
	 */
	public BooleanMovingAverage(int numberOfValues, boolean defaultValue) {
		values = new boolean[numberOfValues];

		if (defaultValue) {
			Arrays.fill(values, defaultValue);
			countTrue = numberOfValues;
		}
	}

	/**
	 * Inserts the given value and replaces the existing value at the current index and increases the index.
	 * If the index exceeds the length it restarts to override the value from the beginning like a circular list.
	 * @param value
	 */
	public void insertValue(boolean value) {
		boolean oldValue = values[index];
		values[index] = value;
		index = (index + 1) % values.length;

		if (oldValue) {
			countTrue--;
		}
		if (value) {
			countTrue++;
		}
	}

	public float getAverage() {
		return ((float) countTrue) / values.length;
	}
}
