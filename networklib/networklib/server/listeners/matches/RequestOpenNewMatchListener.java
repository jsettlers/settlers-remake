package networklib.server.listeners.matches;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.GenericDeserializer;
import networklib.channel.listeners.PacketChannelListener;
import networklib.server.IServerManager;
import networklib.server.game.Match;
import networklib.server.game.Player;
import networklib.server.packets.MatchInfoPacket;
import networklib.server.packets.OpenNewMatchPacket;

/**
 * This listener is called when a client request to open up a new {@link Match}. After the match has successfully been created, the client will
 * receive a {@link MatchInfoPacket}.
 * 
 * @author Andreas Eberle
 * 
 */
public class RequestOpenNewMatchListener extends PacketChannelListener<OpenNewMatchPacket> {

	private final IServerManager serverManager;
	private final Player player;

	public RequestOpenNewMatchListener(IServerManager serverManager, Player player) {
		super(NetworkConstants.Keys.REQUEST_OPEN_NEW_MATCH, new GenericDeserializer<OpenNewMatchPacket>(OpenNewMatchPacket.class));
		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	protected void receivePacket(OpenNewMatchPacket packet) throws IOException {
		serverManager.createNewMatch(packet, player);
	}

}
