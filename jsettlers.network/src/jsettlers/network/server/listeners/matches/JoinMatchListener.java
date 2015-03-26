package jsettlers.network.server.listeners.matches;

import java.io.IOException;

import jsettlers.network.NetworkConstants;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.common.packets.IdPacket;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.listeners.PacketChannelListener;
import jsettlers.network.server.IServerManager;
import jsettlers.network.server.match.Player;

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
