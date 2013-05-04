package networklib.channel.ping;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.channel.packet.Packet;

/**
 * This packet is used to determine the ping of a channel.
 * 
 * @author Andreas Eberle
 * 
 */
public class PingPacket extends Packet {
	private long senderTime;
	private long receiverTime;

	public PingPacket() {
	}

	public PingPacket(long senderTime, long receiverTime) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (receiverTime ^ (receiverTime >>> 32));
		result = prime * result + (int) (senderTime ^ (senderTime >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PingPacket other = (PingPacket) obj;
		if (receiverTime != other.receiverTime)
			return false;
		if (senderTime != other.senderTime)
			return false;
		return true;
	}
}
