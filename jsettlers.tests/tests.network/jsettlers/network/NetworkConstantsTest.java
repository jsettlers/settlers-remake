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
package jsettlers.network;

import static org.junit.Assert.assertEquals;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import jsettlers.network.NetworkConstants;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.NetworkConstants.ENetworkMessage;

import org.junit.Test;

/**
 * This class tests the {@link NetworkConstants} class.
 * 
 * @author Andreas Eberle
 * 
 */
public class NetworkConstantsTest {

	private final DataInputStream in;
	private final DataOutputStream out;

	public NetworkConstantsTest() throws IOException {
		PipedInputStream in = new PipedInputStream();
		PipedOutputStream out = new PipedOutputStream(in);

		this.in = new DataInputStream(in);
		this.out = new DataOutputStream(out);
	}

	@Test
	public void testKeysSerialization() throws IOException {
		for (ENetworkKey currKey : ENetworkKey.values()) {
			currKey.writeTo(out);
			ENetworkKey readKey = ENetworkKey.readFrom(in);

			assertEquals(currKey, readKey);
		}

		assertEquals(0, in.available());
	}

	@Test
	public void testMessagesSerialization() throws IOException {
		for (ENetworkMessage currKey : ENetworkMessage.values()) {
			currKey.writeTo(out);
			ENetworkMessage readKey = ENetworkMessage.readFrom(in);

			assertEquals(currKey, readKey);
		}

		assertEquals(0, in.available());
	}
}
