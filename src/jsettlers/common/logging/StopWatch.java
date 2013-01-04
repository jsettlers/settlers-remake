package jsettlers.common.logging;

/**
 * this class implements a simple stop watch.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class StopWatch {

	private long start;

	public StopWatch() {
		restart();
	}

	/**
	 * saves the current time.
	 */
	public final void restart() {
		start = now();
	}

	/**
	 * calculates the difference from start to now and prints the result.
	 * 
	 * @param leadingText
	 *            text to be pretended to the measured difference
	 */
	public void stop(String leadingText) {
		System.out.println(leadingText + ": " + getDiff() + " " + getUnit());
	}

	protected abstract String getUnit();

	/**
	 * 
	 * @return Returns the difference in time from the start (or last restart) to now.
	 */
	public final long getDiff() {
		return now() - start;
	}

	/**
	 * 
	 * @return Returns the current time in the unit of this {@link StopWatch}.
	 */
	public abstract long now();
}
