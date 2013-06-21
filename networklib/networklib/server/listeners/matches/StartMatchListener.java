package networklib.server.listeners.matches;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.NetworkConstants.ENetworkKey;
import networklib.common.packets.MatchInfoPacket;
import networklib.infrastructure.channel.GenericDeserializer;
import networklib.infrastructure.channel.listeners.PacketChannelListener;
import networklib.infrastructure.channel.packet.EmptyPacket;
import networklib.server.IServerManager;
import networklib.server.game.Match;
import networklib.server.game.Player;

/**
 * This listener is called when a client request to open up a new {@link Match}. After the match has successfully been created, the client will
 * receive a {@link MatchInfoPacket}.
 * 
 * @author Andreas Eberle
 * 
 */
public class StartMatchListener extends PacketChannelListener<EmptyPacket> {

	private final IServerManager serverManager;
	private final Player player;

	public StartMatchListener(IServerManager serverManager, Player player) {
		super(NetworkConstants.ENetworkKey.REQUEST_START_MATCH, new GenericDeserializer<EmptyPacket>(EmptyPacket.class));
		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	protected void receivePacket(ENetworkKey key, EmptyPacket packet) throws IOException {
		serverManager.startMatch(player);
	}

}
