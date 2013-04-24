package networklib.channel;

import java.io.IOException;

/**
 * This interface defines the method need to send a {@link Packet} object.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IPacketSendable {
	/**
	 * Sends the given {@link Packet}.
	 * 
	 * @param packet
	 *            The {@link Packet} to be sent.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	void sendPacket(Packet packet) throws IOException;
}
