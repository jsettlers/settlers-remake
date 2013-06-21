package networklib.client.time;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface ISynchronizableClock {

	/**
	 * 
	 * @return Returns the clock's current time.
	 */
	int getTime();

	/**
	 * Pauses the clock for at least the given period of time.
	 * 
	 * @param pauseTime
	 *            milliseconds to pause the game
	 */
	void pauseClockFor(int timeDelta);

}
