package networklib.infrastructure.channel.ping;

import networklib.infrastructure.channel.Channel;

/**
 * This class holds information about the round trip time on the {@link Channel}.
 * 
 * @author Andreas Eberle
 * 
 */
public class RoundTripTime {
	private final long lastUpdated;
	private final int rtt;
	private final int jitter;
	private final int averagedJitter;

	public RoundTripTime(long lastUpdated, int rtt, int jitter, int averagedJitter) {
		this.lastUpdated = lastUpdated;
		this.rtt = rtt;
		this.jitter = jitter;
		this.averagedJitter = averagedJitter;
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

	/**
	 * 
	 * @return Returns the jittering that is currently noticed on the {@link Channel}.
	 */
	public int getJitter() {
		return jitter;
	}

	/**
	 * 
	 * @return Returns an averaged jittering value.
	 */
	public int getAveragedJitter() {
		return averagedJitter;
	}
}
