package jsettlers.network.infrastructure.channel;

import java.io.DataInputStream;

import jsettlers.network.NetworkConstants.ENetworkKey;

/**
 * This interface defines a listener for the {@link Channel} class. Objects implementing this interface may receive incoming data from the
 * {@link Channel}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IChannelListener {
	/**
	 * Defines the types of data this {@link IChannelListener} will receive.
	 * 
	 * @return Returns an array of keys identifying message types this listener will receive.
	 */
	ENetworkKey[] getKeys();

	/**
	 * This method is called when the {@link Channel} received a message of the given type and the given length. Access to the data is supplied by the
	 * given {@link DataInputStream}.
	 * 
	 * @param key
	 *            The key of the message.
	 * @param length
	 *            Number of bytes on the stream.
	 * @param stream
	 *            The {@link DataInputStream} offering the data.
	 * 
	 * @throws Exception
	 *             If an I/O error occurs.
	 */
	void receive(ENetworkKey key, int length, DataInputStream stream) throws Exception;
}
