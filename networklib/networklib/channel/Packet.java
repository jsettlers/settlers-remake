package networklib.channel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class defines a packet that can be send via a {@link Channel}
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class Packet {

	private final int key;

	/**
	 * Creates a new {@link Packet} with the given key identifying the type of this {@link Packet}.
	 * 
	 * @param key
	 *            The key to identify the type of this packet.
	 */
	public Packet(int key) {
		this.key = key;
	}

	/**
	 * Gives the key identifying the type of this {@link Packet}.
	 * 
	 * @return Returns the id of the type identifying this {@link Packet}.
	 */
	public int getKey() {
		return key;
	}

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
	 * 
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public abstract void deserialize(DataInputStream dis) throws IOException;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key;
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
		Packet other = (Packet) obj;
		if (key != other.key)
			return false;
		return true;
	}
}
