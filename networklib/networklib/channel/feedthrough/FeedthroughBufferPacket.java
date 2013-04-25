package networklib.channel.feedthrough;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.Packet;

/**
 * This class extends the {@link Packet} class and does not really deserialize the data. It just stores the bytes to write them back on the stream
 * again.
 * 
 * @author Andreas Eberle
 * 
 */
public final class FeedthroughBufferPacket extends FeedthroughablePacket {
	private byte[] data;

	public FeedthroughBufferPacket() {
		super(NetworkConstants.Keys.SYNCHRONOUS_TASK);
	}

	@Override
	protected void serializeData(DataOutputStream dos) throws IOException {
		dos.write(data);
	}

	@Override
	protected void deserializeData(int length, DataInputStream dis) throws IOException {
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

}
