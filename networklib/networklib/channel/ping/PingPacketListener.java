package networklib.channel.ping;

import java.io.IOException;

import networklib.channel.Channel;
import networklib.channel.IDeserializingable;
import networklib.channel.NetworkConstants;
import networklib.channel.listeners.PacketChannelListener;

/**
 * {@link PacketChannelListener} to receive and send {@link PingPacket}s.
 * 
 * @author Andreas Eberle
 * 
 */
public class PingPacketListener extends PacketChannelListener<PingPacket> {

	private final Channel channel;

	private RoundTripTime currRtt;

	@SuppressWarnings("unchecked")
	public PingPacketListener(Channel channel) {
		super(new int[] { NetworkConstants.Keys.PING }, new IDeserializingable[] { PingPacket.PING_PACKET_DESERIALIZER });

		this.channel = channel;
		channel.registerListener(this);
	}

	@Override
	protected void receivePacket(PingPacket receivedPing) {
		long now = System.currentTimeMillis();
		int rtt = (int) (now - receivedPing.getReceiverTime());
		currRtt = new RoundTripTime(now, rtt);

		sendPing(receivedPing.getSenderTime());
	}

	private void sendPing(long receiverTime) {
		try {
			channel.sendPacket(new PingPacket(System.currentTimeMillis(), receiverTime));
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	/**
	 * Gets the round trip time of this {@link Channel}.
	 * 
	 * @return Returns the current {@link RoundTripTime}.
	 */
	public RoundTripTime getRoundTripTime() {
		return currRtt;
	}

	/**
	 * Initialize the pinging by sending a first {@link PingPacket}.
	 */
	public void initPinging() {
		sendPing(0);
	}
}
