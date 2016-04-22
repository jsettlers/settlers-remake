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
package jsettlers.network.infrastructure.channel;

import java.io.DataInputStream;

import jsettlers.network.NetworkConstants.ENetworkKey;

/**
 * This interface defines a listener for the {@link Channel} class. Objects implementing this interface may receive incoming data from the
 * {@link Channel}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IChannelListener {
	/**
	 * Defines the types of data this {@link IChannelListener} will receive.
	 * 
	 * @return Returns an array of keys identifying message types this listener will receive.
	 */
	ENetworkKey[] getKeys();

	/**
	 * This method is called when the {@link Channel} received a message of the given type and the given length. Access to the data is supplied by the
	 * given {@link DataInputStream}.
	 * 
	 * @param key
	 *            The key of the message.
	 * @param length
	 *            Number of bytes on the stream.
	 * @param stream
	 *            The {@link DataInputStream} offering the data.
	 * 
	 * @throws Exception
	 *             If an I/O error occurs.
	 */
	void receive(ENetworkKey key, int length, DataInputStream stream) throws Exception;
}
