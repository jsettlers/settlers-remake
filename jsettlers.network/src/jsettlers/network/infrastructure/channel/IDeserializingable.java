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
import java.io.IOException;

import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.infrastructure.channel.packet.Packet;

/**
 * This interface defines a method to deserialize {@link Packet} data received by a {@link Channel}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IDeserializingable<T extends Packet> {

	/**
	 * Deserialize the data with the given key and number of bytes (length) from the given stream.
	 * 
	 * @param key
	 *            The key of the {@link Packet} identifying it's purpose.
	 * @param dis
	 *            The {@link DataInputStream} offering the serialized data of the Packet.
	 * @return
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	T deserialize(ENetworkKey key, DataInputStream dis) throws IOException;
}
