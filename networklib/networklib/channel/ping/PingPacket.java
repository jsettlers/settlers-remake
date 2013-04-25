package networklib.channel.ping;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.IDeserializingable;
import networklib.channel.Packet;

/**
 * This packet is used to determine the ping of a channel.
 * 
 * @author Andreas Eberle
 * 
 */
public class PingPacket extends Packet {
	public static IDeserializingable<PingPacket> PING_PACKET_DESERIALIZER = new IDeserializingable<PingPacket>() {
		@Override
		public PingPacket deserialize(int key, DataInputStream dis) throws IOException {
			PingPacket packet = new PingPacket();
			packet.deserialize(dis);
			return packet;
		}
	};

	private long senderTime;
	private long receiverTime;

	public PingPacket() {
		super(NetworkConstants.Keys.PING);
	}

	public PingPacket(long senderTime, long receiverTime) {
		super(NetworkConstants.Keys.PING);
		this.senderTime = senderTime;
		this.receiverTime = receiverTime;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeLong(senderTime);
		dos.writeLong(receiverTime);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		senderTime = dis.readLong();
		receiverTime = dis.readLong();
	}

	public long getSenderTime() {
		return senderTime;
	}

	public void setSenderTime(long senderTime) {
		this.senderTime = senderTime;
	}

	public long getReceiverTime() {
		return receiverTime;
	}

	public void setReceiverTime(long receiverTime) {
		this.receiverTime = receiverTime;
	}

}
