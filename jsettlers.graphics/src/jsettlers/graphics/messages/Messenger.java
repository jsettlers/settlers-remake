/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.graphics.messages;

import java.util.LinkedList;
import java.util.List;

import jsettlers.common.menu.messages.IMessage;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.statistics.IGameTimeProvider;

/**
 * This is a messenger, that lets you display messages on the users screen.
 * 
 * @author michael
 */
public class Messenger {

	private final LinkedList<IMessage> messages = new LinkedList<IMessage>();
	private final IGameTimeProvider gameTimeProvider;
	private int latestTickTime;
	private int focusPos = 0;

	public Messenger(IGameTimeProvider gameTimeProvider) {
		this.gameTimeProvider = gameTimeProvider;
		this.latestTickTime = gameTimeProvider.getGameTime();
	}

	/**
	 * Gets a list of messages that should be displayed to the user at the moment. It may be long, because only the first messages are displayed.
	 * <p>
	 * The messages have to be sorted by age, the one with the lowest age first.
	 * 
	 * @return The messages to display.
	 */
	public List<IMessage> getMessages() {
		return messages;
	}

	public boolean addMessage(IMessage message) {
		if (isNews(message)) {
			messages.addFirst(message);
			if (messages.size() > IMessage.MAX_MESSAGES)
				messages.removeLast();
			focusPos = 0;
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public void doTick() {
		if (!gameTimeProvider.isGamePausing()) {
			// update message ages
			int interval = gameTimeProvider.getGameTime() - latestTickTime;
			for (IMessage m : messages) {
				if (m.ageBy(interval) > IMessage.MESSAGE_TTL)
					// remove all remaining messages, assuming they in order
					while (!messages.pollLast().equals(m))
						;
			}
		}
		latestTickTime = gameTimeProvider.getGameTime();
	}

	/**
	 * Determines whether a given message is worth its display, considering nature and 
	 * content of the messages already shown. 
	 * @param msg {@link IMessage} to be inspected.
	 * @return true, if message should be displayed.
	 */
	boolean isNews(IMessage msg) {
		for (IMessage m : messages) {
			if (msg.duplicates(m)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a position this messenger currently considers noteworthy.
	 * I.e. location of latest incoming message, then that of respectively next,
	 * when repeatedly called.
	 * @return A {@link ShortPoint2D} retrieved from a message.
	 */
	public ShortPoint2D getPosition() {
		if (!messages.isEmpty()) {
			// retrieve nth message's position and increment n
			return messages.get(focusPos++ % messages.size()).getPosition();
		}
		return null;
	}

}
