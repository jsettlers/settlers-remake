package go.region;

/**
 * This class represents a interval in the space of double values.
 * @author michael
 *
 */
public class DoubleRange {
	private final double min;
	private final double max;

	/**
	 * Creates a new range of doubles. if min is bigger than max, a intervall
	 * with length 0 is created around the average of both.
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
		return this.min;
	}

	/**
	 * Gets the maximal value, that is the end of the interval.
	 * 
	 * @return The max value given to the constructor.
	 */
	public double getMax() {
		return this.max;
	}

	/**
	 * Constraints a value so that it lies inside the current interval and is as
	 * close to the given value as possible.
	 * 
	 * @param x
	 *            The value to constraint.
	 * @return The closest point inside the interval to the parameter x.
	 */
	public double constraint(double x) {
		if (x < this.min) {
			return this.min;
		} else if (x > this.max) {
			return this.max;
		} else {
			return x;
		}
	}

	/**
	 * Gets a new Range with the given minimum as minimum, and the current
	 * maximum as maximum. Inf the current maximum is to small, the new one is
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
