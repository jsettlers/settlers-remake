package jsettlers.common.logging;

/**
 * this class implements a simple stop watch.
 * 
 * @author Andreas Eberle
 * 
 */
public class MilliStopWatch extends StopWatch {

	@Override
	public void restart() {
		start = System.currentTimeMillis();
	}

	@Override
	public void stop() {
		diff = System.currentTimeMillis() - start;
	}

	@Override
	protected String getUnit() {
		return "ms";
	}

}
