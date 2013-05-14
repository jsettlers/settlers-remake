package networklib.client.time;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface ISynchronizableClock {

	int getTime();

	void stopClockFor(int timeDelta);

}
