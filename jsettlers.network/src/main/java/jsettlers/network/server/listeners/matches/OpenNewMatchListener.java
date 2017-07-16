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
package jsettlers.network.server.listeners.matches;

import java.io.IOException;

import jsettlers.network.NetworkConstants;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.common.packets.MatchInfoPacket;
import jsettlers.network.common.packets.OpenNewMatchPacket;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.listeners.PacketChannelListener;
import jsettlers.network.server.IServerManager;
import jsettlers.network.server.match.Match;
import jsettlers.network.server.match.Player;

/**
 * This listener is called when a client request to open up a new {@link Match}. After the match has successfully been created, the client will
 * receive a {@link MatchInfoPacket}.
 * 
 * @author Andreas Eberle
 * 
 */
public class OpenNewMatchListener extends PacketChannelListener<OpenNewMatchPacket> {

	private final IServerManager serverManager;
	private final Player player;

	public OpenNewMatchListener(IServerManager serverManager, Player player) {
		super(NetworkConstants.ENetworkKey.REQUEST_OPEN_NEW_MATCH, new GenericDeserializer<>(OpenNewMatchPacket.class));
		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	protected void receivePacket(ENetworkKey key, OpenNewMatchPacket packet) throws IOException {
		serverManager.createNewMatch(packet, player);
	}

}
