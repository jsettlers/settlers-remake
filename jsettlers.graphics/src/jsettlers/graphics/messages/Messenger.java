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
