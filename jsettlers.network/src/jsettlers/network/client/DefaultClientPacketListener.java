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
package jsettlers.network.client;

import java.io.IOException;

import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.client.receiver.IPacketReceiver;
import jsettlers.network.infrastructure.channel.IDeserializingable;
import jsettlers.network.infrastructure.channel.listeners.PacketChannelListener;
import jsettlers.network.infrastructure.channel.packet.Packet;

/**
 * Default {@link PacketChannelListener} used by the {@link NetworkClient} to dispatch received {@link Packet}s to a {@link IPacketReceiver}.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 *            The type of Packets that can be received by this {@link DefaultClientPacketListener}.
 */
public class DefaultClientPacketListener<T extends Packet> extends PacketChannelListener<T> {

	private IPacketReceiver<T> receiver;

	public DefaultClientPacketListener(ENetworkKey key, IDeserializingable<T> deserializer, IPacketReceiver<T> receiver) {
		super(key, deserializer);
		this.receiver = receiver;
	}

	@Override
	protected void receivePacket(ENetworkKey key, T packet) throws IOException {
		if (receiver != null)
			receiver.receivePacket(packet);
	}

}
