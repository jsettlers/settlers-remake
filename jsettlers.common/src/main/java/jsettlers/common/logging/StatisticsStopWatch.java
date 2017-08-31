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
package jsettlers.common.logging;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * This class implements a simple stop watch that records the time in milliseconds and prints
 * the average, mean, min and max of all measured measurements.
 *
 * @author codingberlin
 */
public class StatisticsStopWatch extends StopWatch {

	private final List<Long> measurements = new Vector<>();

	@Override
	public long now() {
		return System.currentTimeMillis();
	}

	@Override
	protected String getUnit() {
		return "ms";
	}

	@Override
	public void stop(String leadingText) {
		measurements.add(getDiff());
		Collections.sort(measurements);
	}

	@Override
	public String toString() {
		if (measurements.isEmpty()) {
			return " -> no measurements taken yet";
		}
		return " -> number of measurements: " + measurements.size()
				+ ", min: " + measurements.get(0) + " " + getUnit()
				+ ", average: " + calculateAverage(measurements) + " " + getUnit()
				+ ", median: " + getMedian() + " " + getUnit()
				+ ", max: " + getMax() + " " + getUnit();
	}

	public long getMedian() {
		return measurements.get((int) Math.floor(measurements.size() / 2));
	}

	public long getMax() {
		return measurements.get(measurements.size() - 1);
	}

	private double calculateAverage(List<Long> measurements) {
		long sum = 0;
		if(!measurements.isEmpty()) {
			for (Long measurement : measurements) {
				sum += measurement;
			}
			return sum / measurements.size();
		}
		return sum;
	}
}
