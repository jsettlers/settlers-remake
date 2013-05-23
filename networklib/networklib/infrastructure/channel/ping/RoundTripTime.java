package networklib.infrastructure.channel.ping;

public class RoundTripTime {
	private final long lastUpdated;
	private final int rtt;

	public RoundTripTime(long lastUpdated, int rtt) {
		this.lastUpdated = lastUpdated;
		this.rtt = rtt;
	}

	public long getLastUpdated() {
		return lastUpdated;
	}

	public int getRtt() {
		return rtt;
	}
}
