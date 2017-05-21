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

import jsettlers.network.NetworkConstants;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.client.receiver.IPacketReceiver;
import jsettlers.network.common.packets.MatchInfoUpdatePacket;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.listeners.PacketChannelListener;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class MatchInfoUpdatedListener extends PacketChannelListener<MatchInfoUpdatePacket> {
	private final NetworkClient client;
	private final IPacketReceiver<MatchInfoUpdatePacket> listener;

	public MatchInfoUpdatedListener(NetworkClient client, IPacketReceiver<MatchInfoUpdatePacket> listener) {
		super(NetworkConstants.ENetworkKey.MATCH_INFO_UPDATE, new GenericDeserializer<>(MatchInfoUpdatePacket.class));

		this.client = client;
		this.listener = listener;
	}

	@Override
	protected void receivePacket(ENetworkKey key, MatchInfoUpdatePacket packet) throws IOException {
		client.matchInfoUpdated(packet);

		if (listener != null)
			listener.receivePacket(packet);
	}

}
