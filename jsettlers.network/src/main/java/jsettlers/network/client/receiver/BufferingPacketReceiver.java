/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.network.client.receiver;

import java.util.LinkedList;
import java.util.List;

import jsettlers.network.infrastructure.channel.packet.Packet;

/**
 * This class implements the {@link IPacketReceiver} interface and buffers the received {@link Packet}s. This implementation is generic and can be
 * used for any subtype of {@link Packet}.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 */
public class BufferingPacketReceiver<T extends Packet> implements IPacketReceiver<T> {

	private List<T> buffer = new LinkedList<>();

	@Override
	public void receivePacket(T packet) {
		buffer.add(packet);
	}

	public List<T> popBufferedPackets() {
		List<T> temp = buffer;
		buffer = new LinkedList<T>();
		return temp;
	}
}
