package networklib.channel.feedthrough;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.channel.Packet;

/**
 * Subclasses of this class extend the {@link Packet} class to send custom data over the server without the need to let the server know how to
 * deserialize the subclass.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class FeedthroughablePacket extends Packet {

	public FeedthroughablePacket(int key) {
		super(key);
	}

	@Override
	public final void serialize(DataOutputStream dos) throws IOException {
		// write the child object to the buffer to get it's length
		final ByteArrayOutputStream byteBufferStream = new ByteArrayOutputStream();
		final DataOutputStream bufferDos = new DataOutputStream(byteBufferStream);

		serializeData(bufferDos);
		bufferDos.flush();

		int length = byteBufferStream.size();

		dos.writeInt(length);
		byteBufferStream.writeTo(dos);
	}

	protected abstract void serializeData(DataOutputStream dos) throws IOException;

	@Override
	public final void deserialize(DataInputStream dis) throws IOException {
		int length = dis.readInt();
		deserializeData(length, dis);

		System.out.println("deserialized packet with key: " + getKey() + " and length: " + length);
	}

	protected abstract void deserializeData(int length, DataInputStream dis) throws IOException;

}
