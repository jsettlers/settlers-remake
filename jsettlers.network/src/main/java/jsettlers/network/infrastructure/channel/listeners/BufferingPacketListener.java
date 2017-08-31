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
package jsettlers.network.infrastructure.channel.listeners;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.infrastructure.channel.IDeserializingable;
import jsettlers.network.infrastructure.channel.packet.Packet;

/**
 * This implementation of a {@link PacketChannelListener} collects the received packets to buffer them.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 */
public class BufferingPacketListener<T extends Packet> extends PacketChannelListener<T> {

	private final Object lock = new Object();
	private List<T> packets = new LinkedList<>();

	public BufferingPacketListener(ENetworkKey key, IDeserializingable<T> deserializer) {
		super(key, deserializer);
	}

	@Override
	protected void receivePacket(ENetworkKey key, T deserialized) throws IOException {
		synchronized (lock) {
			packets.add(deserialized);
		}
	}

	public List<T> popBufferedPackets() {
		synchronized (lock) {
			List<T> temp = packets;
			packets = new LinkedList<T>();
			return temp;
		}
	}
}
