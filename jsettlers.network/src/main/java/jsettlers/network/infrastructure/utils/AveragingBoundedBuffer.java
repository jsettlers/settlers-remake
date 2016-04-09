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
package jsettlers.network.infrastructure.utils;

/**
 * Cyclic buffer calculating the average of the last {@code n} given numbers, where {@code n} is a configurable number.
 * 
 * @author Andreas Eberle
 * 
 */
public class AveragingBoundedBuffer {
	private final int length;
	private final int[] buffer;
	private int index = 0;
	private int sum = 0;

	public AveragingBoundedBuffer(int length) {
		this.length = length;
		this.buffer = new int[length];
	}

	public void insert(int value) {
		sum = sum - buffer[index] + value;
		buffer[index] = value;

		index = (index + 1) % length;
	}

	public int getAverage() {
		return sum / length;
	}

	public int getLength() {
		return length;
	}
}