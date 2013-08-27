package jsettlers.graphics.map;

import jsettlers.graphics.messages.Message;

/**
 * Interface defining a method to send messages to the user.
 * 
 * @author Andreas Eberle
 */
public interface IMessenger {
	/**
	 * Adds a text message to be displayed on the screen.
	 * 
	 * @param message
	 *            The message to display.
	 */
	void showMessage(Message message);
}
