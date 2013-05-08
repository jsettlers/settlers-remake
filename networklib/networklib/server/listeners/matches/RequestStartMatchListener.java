package networklib.server.listeners.matches;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.GenericDeserializer;
import networklib.channel.listeners.PacketChannelListener;
import networklib.channel.packet.EmptyPacket;
import networklib.server.IServerManager;
import networklib.server.game.Match;
import networklib.server.game.Player;
import networklib.server.packets.MatchInfoPacket;

/**
 * This listener is called when a client request to open up a new {@link Match}. After the match has successfully been created, the client will
 * receive a {@link MatchInfoPacket}.
 * 
 * @author Andreas Eberle
 * 
 */
public class RequestStartMatchListener extends PacketChannelListener<EmptyPacket> {

	private final IServerManager serverManager;
	private final Player player;

	public RequestStartMatchListener(IServerManager serverManager, Player player) {
		super(NetworkConstants.Keys.REQUEST_START_MATCH, new GenericDeserializer<EmptyPacket>(EmptyPacket.class));
		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	protected void receivePacket(int key, EmptyPacket packet) throws IOException {
		serverManager.startMatch(player);
	}

}
