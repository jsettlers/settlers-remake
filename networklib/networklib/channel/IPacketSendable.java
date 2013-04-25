package networklib.channel;

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
	 * @return true if the {@link Packet} has been sent successfully<br>
	 *         false if it hasn't been sent. This can happen if the {@link IPacketSendable} is already closed or an error occurred.
	 */
	boolean sendPacket(Packet packet);

	/**
	 * 
	 * @return True if this {@link IPacketSendable} is already closed.<br>
	 *         False if it is not closed.
	 */
	boolean isClosed();
}
