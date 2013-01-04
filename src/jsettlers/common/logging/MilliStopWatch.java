package jsettlers.common.logging;

/**
 * This class implements a simple stop watch that records the time in milliseconds.
 * 
 * @author Andreas Eberle
 * 
 */
public class MilliStopWatch extends StopWatch {

	@Override
	public long now() {
		return System.currentTimeMillis();
	}

	@Override
	protected String getUnit() {
		return "ms";
	}
}
