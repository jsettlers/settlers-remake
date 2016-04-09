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
package jsettlers.network.infrastructure.channel.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.network.infrastructure.channel.Channel;

/**
 * This class defines a packet that can be send via a {@link Channel}
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class Packet {

	/**
	 * Serializes this object to the given {@link DataOutputStream}.
	 * 
	 * @param oos
	 *            The object will be written to this stream.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public abstract void serialize(DataOutputStream dos) throws IOException;

	/**
	 * Deserializes an object of this type from the given {@link DataInputStream}.
	 * 
	 * @param dis
	 *            The {@link DataInputStream} that's supplying the serialized data.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public abstract void deserialize(DataInputStream dis) throws IOException;

	@Override
	public abstract boolean equals(Object o);

	@Override
	public abstract int hashCode();
}
