package jsettlers.common.logging;

/**
 * This class implements a simple stop watch that records the time in nanoseconds.
 * 
 * @author Andreas Eberle
 * 
 */
public class NanoStopWatch extends StopWatch {

	@Override
	public long now() {
		return System.nanoTime();
	}

	@Override
	protected String getUnit() {
		return "ns";
	}

}
