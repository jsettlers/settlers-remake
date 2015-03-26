package jsettlers.network.infrastructure.channel.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.network.infrastructure.channel.Channel;

/**
 * This class defines a packet that can be send via a {@link Channel}
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class Packet {

	/**
	 * Serializes this object to the given {@link DataOutputStream}.
	 * 
	 * @param oos
	 *            The object will be written to this stream.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public abstract void serialize(DataOutputStream dos) throws IOException;

	/**
	 * Deserializes an object of this type from the given {@link DataInputStream}.
	 * 
	 * @param dis
	 *            The {@link DataInputStream} that's supplying the serialized data.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public abstract void deserialize(DataInputStream dis) throws IOException;

	@Override
	public abstract boolean equals(Object o);

	@Override
	public abstract int hashCode();
}
