package jsettlers.network.infrastructure.channel;

import java.io.DataInputStream;
import java.io.IOException;

import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.infrastructure.channel.packet.Packet;

/**
 * This interface defines a method to deserialize {@link Packet} data received by a {@link Channel}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IDeserializingable<T extends Packet> {

	/**
	 * Deserialize the data with the given key and number of bytes (length) from the given stream.
	 * 
	 * @param key
	 *            The key of the {@link Packet} identifying it's purpose.
	 * @param dis
	 *            The {@link DataInputStream} offering the serialized data of the Packet.
	 * @return
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	T deserialize(ENetworkKey key, DataInputStream dis) throws IOException;
}
