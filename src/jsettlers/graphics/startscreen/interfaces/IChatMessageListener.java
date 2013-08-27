package jsettlers.graphics.startscreen.interfaces;

/**
 * This interface defines a method to receive chat messages.
 * 
 * @author Andreas Eberle
 */
public interface IChatMessageListener {

	/**
	 * This method is called when a chat message has been received.
	 * 
	 * @param authorId
	 *            Id of the author of the message.
	 * @param message
	 *            Received message.
	 */
	void chatMessageReceived(String authorId, String message);

	/**
	 * This method is called when a predefined message has been issued by the
	 * server.
	 * 
	 * @param author
	 *            The player connected to the message (for example the player
	 *            that left).<br>
	 *            Or null, if the message is player-independent.
	 * @param message
	 *            The predefined message that has been received.
	 */
	void systemMessageReceived(IMultiplayerPlayer author,
	        ENetworkMessage message);

}
