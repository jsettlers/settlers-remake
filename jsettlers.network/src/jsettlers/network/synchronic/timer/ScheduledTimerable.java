package jsettlers.network.synchronic.timer;

/**
 * Container class for a {@link INetworkTimerable}, it's periodic execution delay and it's current execution delay.
 * 
 * @author Andreas Eberle
 * 
 */
public final class ScheduledTimerable {

	private final INetworkTimerable timerable;
	private final short delay;
	private short currDelay;

	public ScheduledTimerable(INetworkTimerable timerable, short delay) {
		this.timerable = timerable;
		this.delay = delay;
		this.currDelay = delay;
	}

	public INetworkTimerable getTimerable() {
		return timerable;
	}

	/**
	 * Checks if this task needs to be executed. (Is able to execute tasks serveral times if needed
	 * 
	 * @param timeSlice
	 *            number of milliseconds of the game time that expired since the last call.
	 */
	public void checkExecution(short timeSlice) {
		currDelay -= timeSlice;
		while (currDelay <= 0) {
			currDelay += delay;
			timerable.timerEvent();
		}
	}
}
