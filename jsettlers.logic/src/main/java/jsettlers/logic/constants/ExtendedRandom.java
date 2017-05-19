/*******************************************************************************
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
 *******************************************************************************/
package jsettlers.logic.constants;

import java.util.Random;

/**
 * Extended version of java.util.Random with some more functions.
 * 
 * @author Andreas Eberle
 *
 */
public class ExtendedRandom extends Random {
	private static final long serialVersionUID = -2814532519838158362L;

	public ExtendedRandom(long seed) {
		super(seed);
	}

	/**
	 * Returns a random number in the interval [min, max].
	 * 
	 * @param min
	 *            Minimum value (inclusive)
	 * @param max
	 *            Maximum value (inclusive)
	 * @return
	 */
	public int nextInt(int min, int max) {
		return min + nextInt(max - min + 1);
	}
}
