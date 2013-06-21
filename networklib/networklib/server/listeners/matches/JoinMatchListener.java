package networklib.server.listeners.matches;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.NetworkConstants.ENetworkKey;
import networklib.common.packets.IdPacket;
import networklib.infrastructure.channel.GenericDeserializer;
import networklib.infrastructure.channel.listeners.PacketChannelListener;
import networklib.server.IServerManager;
import networklib.server.game.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class JoinMatchListener extends PacketChannelListener<IdPacket> {

	private final IServerManager serverManager;
	private final Player player;

	public JoinMatchListener(IServerManager serverManager, Player player) {
		super(NetworkConstants.ENetworkKey.REQUEST_JOIN_MATCH, new GenericDeserializer<IdPacket>(IdPacket.class));
		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	protected void receivePacket(ENetworkKey key, IdPacket packet) throws IOException {
		serverManager.joinMatch(packet.getId(), player);
	}

}
