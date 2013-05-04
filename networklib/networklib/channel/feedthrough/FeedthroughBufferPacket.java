package networklib.channel.feedthrough;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import networklib.channel.packet.Packet;

/**
 * This class extends the {@link Packet} class and does not really deserialize the data. It just stores the bytes to write them back on the stream
 * again.
 * 
 * @author Andreas Eberle
 * 
 */
public final class FeedthroughBufferPacket extends Packet {
	private byte[] data;

	public FeedthroughBufferPacket() {
	}

	public FeedthroughBufferPacket(byte[] data) {
		this.data = data;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.write(data);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		final int length = dis.available();
		this.data = new byte[length];

		int alreadyRead = 0;
		while (length - alreadyRead > 0) {
			int numberOfBytesRead = dis.read(data, alreadyRead, length - alreadyRead);
			if (numberOfBytesRead < 0) {
				throw new IOException("Stream ended to early!");
			}

			alreadyRead += numberOfBytesRead;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
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
		FeedthroughBufferPacket other = (FeedthroughBufferPacket) obj;
		if (!Arrays.equals(data, other.data))
			return false;
		return true;
	}
}
