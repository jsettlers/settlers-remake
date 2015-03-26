package jsettlers.network.infrastructure.channel.ping;

/**
 * Listener to be informed when the ping information is updated.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IPingUpdateListener {
	/**
	 * This method is called when a new round trip time is available.
	 * 
	 * @param rtt
	 *            The new {@link RoundTripTime}.
	 */
	void pingUpdated(RoundTripTime rtt);
}
