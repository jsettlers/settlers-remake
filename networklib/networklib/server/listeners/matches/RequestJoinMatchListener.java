package networklib.server.listeners.matches;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.common.packets.MatchInfoPacket;
import networklib.infrastructure.channel.GenericDeserializer;
import networklib.infrastructure.channel.listeners.PacketChannelListener;
import networklib.server.IServerManager;
import networklib.server.game.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class RequestJoinMatchListener extends PacketChannelListener<MatchInfoPacket> {

	private final IServerManager serverManager;
	private final Player player;

	public RequestJoinMatchListener(IServerManager serverManager, Player player) {
		super(NetworkConstants.Keys.REQUEST_JOIN_MATCH, new GenericDeserializer<MatchInfoPacket>(MatchInfoPacket.class));
		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	protected void receivePacket(int key, MatchInfoPacket packet) throws IOException {
		serverManager.joinMatch(packet, player);
	}

}
