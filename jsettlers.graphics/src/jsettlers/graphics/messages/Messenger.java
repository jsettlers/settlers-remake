package jsettlers.graphics.messages;

import java.util.LinkedList;
import java.util.List;

/**
 * This is a messenger, that lets you display messages on the users screen.
 * 
 * @author michael
 */
public class Messenger {
	private static final int MAX_MESSAGES = 50;
	LinkedList<Message> messages = new LinkedList<Message>();

	/**
	 * Gets a list of messages that should be displayed to the user at the moment. It may be long, because only the first messages are displayed.
	 * <p>
	 * The messages have to be sorted by age, the one with the lowest age first.
	 * 
	 * @return The messages to display.
	 */
	public List<Message> getMessages() {
		return messages;
	}

	public void addMessage(Message message) {
		messages.addFirst(message);
		if (messages.size() > MAX_MESSAGES) {
			messages.removeLast();
		}
	}
}
