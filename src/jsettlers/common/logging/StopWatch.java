package jsettlers.common.logging;

/**
 * this class implements a simple stop watch.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class StopWatch {

	protected long start;
	protected long diff;

	public StopWatch() {
		restart();
	}

	/**
	 * saves the current time.
	 */
	public abstract void restart();

	/**
	 * calculates the difference from start to now
	 */
	public abstract void stop();

	/**
	 * calculates the difference from start to now and prints the result.
	 * 
	 * @param leadingText
	 *            text to be pretended to the measured difference
	 */
	public void stop(String leadingText) {
		stop();
		System.out.println(leadingText + ": " + diff + " " + getUnit());
	}

	protected abstract String getUnit();

	/**
	 * 
	 * @return measured difference.
	 */
	public long getDiff() {
		return diff;
	}
}
