package jsettlers.common.logging;

/**
 * this class implements a simple stop watch.
 * 
 * @author Andreas Eberle
 * 
 */
public class NanoStopWatch extends StopWatch {

	@Override
	public void restart() {
		start = System.nanoTime();
	}

	@Override
	public void stop() {
		diff = System.nanoTime() - start;
	}

	@Override
	protected String getUnit() {
		return "ns";
	}

}
