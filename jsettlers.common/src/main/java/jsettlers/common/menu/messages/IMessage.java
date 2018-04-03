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
package jsettlers.common.menu.messages;

import jsettlers.common.position.ShortPoint2D;

/**
 * This is a message for the user, that is displayed at the users screen.
 * 
 * @author michael
 */
public interface IMessage {
	/**
	 * Number of messages that queue can hold at largest.
	 */
	int MAX_MESSAGES = 16;
	/**
	 * Longest duration for which messages should remain in queue, in milliseconds.
	 */
	long MESSAGE_TTL = 90000;
	/**
	 * Map grid distance beyond which two messages should be considered sufficiently different to be prompted separately.
	 */
	int MESSAGE_DIST_THRESHOLD = 24;

	/**
	 * Gets the type of this message.
	 * 
	 * @return The type the message has.
	 */
	EMessageType getType();

	/**
	 * Gets the age of the message, that is the time since it was sent.
	 * 
	 * @return The time it was sent in milliseconds.
	 */
	int getAge();

	/**
	 * Gets the message content. Only used for chat messages.
	 * 
	 * @return A string that can be displayed to the user. Never null.
	 */
	String getMessage();

	/**
	 * Gets the Player that send the message, as byte. If the message is a status message for the current user, the sender is -1. For attack messages,
	 * this is also used.
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

	/**
	 * Compares this message to another one and decides whether they are redundant.
	 *
	 * @param m The message to compare to.
	 * @return true, if redundant.
	 */
	boolean duplicates(IMessage m);

	/**
	 * Grow older by the specified number of milliseconds.
	 *
	 * @param milliseconds Number of milliseconds this message is to get older by.
	 * @return resulting age in milliseconds.
	 */
	int ageBy(int milliseconds);
}
