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

}
