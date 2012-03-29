package jsettlers.graphics.messages;

import jsettlers.common.position.ShortPoint2D;

/**
 * This is a message for the user, that is displayed at the users screen.
 * 
 * @author michael
 */
public interface Message {
	/**
	 * Gets the type of this message.
	 * 
	 * @return The type the message has.
	 */
	EMessageType getType();

	/**
	 * Gets the age of the message, that is the time since it was sent.
	 * 
	 * @return The time since it was sent in milliseconds.
	 */
	int getAge();

	/**
	 * Gets the message content. Only used for chat messages.
	 * 
	 * @return A string that can be displayed to the user. Never null.
	 */
	String getMessage();

	/**
	 * Gets the Player that send the message, as byte. If the message is a
	 * status message for the current user, the sender is -1. For attack
	 * messages, this is also used.
	 * 
	 * @return The sender.
	 */
	byte getSender();

	/**
	 * gets the position where the event occured.
	 * 
	 * @return A point describing the message position on the map. May be outside the map.
	 */
	ShortPoint2D getPosition();
}
