package networklib.infrastructure.channel.ping;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class RoundTripTime {
	private final long lastUpdated;
	private final int rtt;

	public RoundTripTime(long lastUpdated, int rtt) {
		this.lastUpdated = lastUpdated;
		this.rtt = rtt;
	}

	/**
	 * 
	 * @return Returns the time when this round trip time has been updated as linux timestamp.
	 */
	public long getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * 
	 * @return Returns the round trip time in milliseconds.
	 */
	public int getRtt() {
		return rtt;
	}
}
