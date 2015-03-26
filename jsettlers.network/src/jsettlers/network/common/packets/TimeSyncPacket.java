package jsettlers.network.common.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.network.infrastructure.channel.packet.Packet;

public class TimeSyncPacket extends Packet {

	private int time;

	public TimeSyncPacket() {
	}

	public TimeSyncPacket(int time) {
		this.time = time;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeInt(time);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		time = dis.readInt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + time;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeSyncPacket other = (TimeSyncPacket) obj;
		if (time != other.time)
			return false;
		return true;
	}

	public int getTime() {
		return time;
	}
}
