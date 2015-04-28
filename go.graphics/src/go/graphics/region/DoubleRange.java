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
package go.graphics.region;

/**
 * This class represents a interval in the space of double values.
 * 
 * @author michael
 *
 */
public class DoubleRange {
	private final double min;
	private final double max;

	/**
	 * Creates a new range of doubles. if min is bigger than max, a intervall with length 0 is created around the average of both.
	 * 
	 * @param min
	 *            The min value
	 * @param max
	 *            The max value.
	 */
	DoubleRange(double min, double max) {
		if (min <= max) {
			this.min = min;
			this.max = max;
		} else {
			double average = (min + max / 2);
			this.min = average;
			this.max = average;
		}
	}

	/**
	 * Gets the minimal value, that is the start point of the interval.
	 * 
	 * @return The min value given to the constructor.
	 */
	public double getMin() {
		return min;
	}

	/**
	 * Gets the maximal value, that is the end of the interval.
	 * 
	 * @return The max value given to the constructor.
	 */
	public double getMax() {
		return max;
	}

	/**
	 * Constraints a value so that it lies inside the current interval and is as close to the given value as possible.
	 * 
	 * @param x
	 *            The value to constraint.
	 * @return The closest point inside the interval to the parameter x.
	 */
	public double constraint(double x) {
		if (x < min) {
			return min;
		} else if (x > max) {
			return max;
		} else {
			return x;
		}
	}

	/**
	 * Gets a new Range with the given minimum as minimum, and the current maximum as maximum. Inf the current maximum is to small, the new one is
	 * increased so that a 0-length-interval is created.
	 * 
	 * @param d
	 *            The new minimum.
	 * @return The new Range.
	 */
	public DoubleRange withMinimum(double d) {
		double min = d;
		double max = Math.max(d, this.max);
		return new DoubleRange(min, max);
	}
}
