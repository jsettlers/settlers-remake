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
package jsettlers.network.server.listeners;

import java.io.IOException;

import jsettlers.network.NetworkConstants;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.common.packets.PlayerInfoPacket;
import jsettlers.network.infrastructure.channel.Channel;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.listeners.PacketChannelListener;
import jsettlers.network.infrastructure.channel.packet.EmptyPacket;
import jsettlers.network.infrastructure.channel.reject.RejectPacket;
import jsettlers.network.server.IServerManager;
import jsettlers.network.server.match.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class IdentifyUserListener extends PacketChannelListener<PlayerInfoPacket> {

	private final Channel channel;
	private final IServerManager serverManager;

	public IdentifyUserListener(Channel channel, IServerManager userAcceptor) {
		super(ENetworkKey.IDENTIFY_USER, new GenericDeserializer<>(PlayerInfoPacket.class));
		this.channel = channel;
		this.serverManager = userAcceptor;
	}

	@Override
	protected void receivePacket(ENetworkKey key, PlayerInfoPacket playerInfo) throws IOException {
		Player player = new Player(playerInfo, channel);
		if (serverManager.acceptNewPlayer(player)) {
			channel.sendPacket(NetworkConstants.ENetworkKey.IDENTIFY_USER, new EmptyPacket());
			serverManager.sendMatchesToPlayer(player);
		} else {
			channel.sendPacket(NetworkConstants.ENetworkKey.REJECT_PACKET, new RejectPacket(NetworkConstants.ENetworkMessage.UNAUTHORIZED,
					NetworkConstants.ENetworkKey.IDENTIFY_USER));
		}
	}
}
