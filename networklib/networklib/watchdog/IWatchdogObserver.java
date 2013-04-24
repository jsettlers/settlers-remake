package networklib.watchdog;

/**
 * Interface for classes that want to be notified by a {@link WatchdogTimer}.
 */
public interface IWatchdogObserver {

	/**
	 * Called when the {@link WatchdogTimer} times out.
	 * 
	 * @param watchdogTimer
	 *            The {@link WatchdogTimer} that timed out.
	 */
	void timeoutOccured(WatchdogTimer watchdogTimer);
}