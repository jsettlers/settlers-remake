package networklib.infrastructure.channel.ping;

import networklib.NetworkConstants;
import networklib.NetworkConstants.ENetworkKey;
import networklib.infrastructure.channel.Channel;
import networklib.infrastructure.channel.GenericDeserializer;
import networklib.infrastructure.channel.listeners.PacketChannelListener;

/**
 * {@link PacketChannelListener} to receive and send {@link PingPacket}s.
 * 
 * @author Andreas Eberle
 * 
 */
public class PingPacketListener extends PacketChannelListener<PingPacket> implements IRoundTripTimeSupplier {

	private final Channel channel;
	private RoundTripTime currRtt = new RoundTripTime(System.currentTimeMillis(), 0);
	private IPingUpdateListener pingUpdateListener = null;

	public PingPacketListener(Channel channel) {
		super(NetworkConstants.ENetworkKey.PING, new GenericDeserializer<PingPacket>(PingPacket.class));

		this.channel = channel;
		channel.registerListener(this);
	}

	@Override
	protected void receivePacket(ENetworkKey key, PingPacket receivedPing) {
		long now = System.currentTimeMillis();
		int rtt = (int) (now - receivedPing.getReceiverTime());
		currRtt = new RoundTripTime(now, rtt);
		System.out.println("Ping: " + rtt);

		sendPing(receivedPing.getSenderTime());

		if (pingUpdateListener != null)
			pingUpdateListener.pingUpdated(currRtt);
	}

	private void sendPing(long receiverTime) {
		channel.sendPacket(NetworkConstants.ENetworkKey.PING, new PingPacket(System.currentTimeMillis(), receiverTime));
	}

	/**
	 * Gets the round trip time of this {@link Channel}.
	 * 
	 * @return Returns the current {@link RoundTripTime}.
	 */
	@Override
	public RoundTripTime getRoundTripTime() {
		return currRtt;
	}

	/**
	 * Initialize the pinging by sending a first {@link PingPacket}.
	 */
	public void initPinging() {
		sendPing(0);
	}

	/**
	 * Sets the {@link IPingUpdateListener} that will be informed on ping updates. Set null to deregister a listener.<br>
	 * Note: Only one listener at a time can be set!
	 * 
	 * @param pingUpdateListener
	 */
	public void setPingUpdateListener(IPingUpdateListener pingUpdateListener) {
		this.pingUpdateListener = pingUpdateListener;
	}
}
