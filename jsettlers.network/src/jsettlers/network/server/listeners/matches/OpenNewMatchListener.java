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
		super(NetworkConstants.ENetworkKey.REQUEST_OPEN_NEW_MATCH, new GenericDeserializer<OpenNewMatchPacket>(OpenNewMatchPacket.class));
		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	protected void receivePacket(ENetworkKey key, OpenNewMatchPacket packet) throws IOException {
		serverManager.createNewMatch(packet, player);
	}

}
