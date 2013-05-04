package networklib.server.listeners.matches;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.IDeserializingable;
import networklib.channel.listeners.PacketChannelListener;
import networklib.channel.packet.EmptyPacket;
import networklib.server.IServerManager;
import networklib.server.game.Player;

/**
 * This {@link PacketChannelListener} handles requests for the matches list and the list of running games of the player.
 * 
 * @author Andreas Eberle
 * 
 */
public class RequestMatchesListener extends PacketChannelListener<EmptyPacket> {

	private final IServerManager serverManager;
	private final Player player;

	@SuppressWarnings("unchecked")
	public RequestMatchesListener(IServerManager serverManager, Player player) {
		super(new int[] { NetworkConstants.Keys.REQUEST_MATCHES, NetworkConstants.Keys.REQUEST_PLAYERS_RUNNING_MATCHES },
				new IDeserializingable[] { EmptyPacket.DEFAULT_DESERIALIZER, EmptyPacket.DEFAULT_DESERIALIZER });

		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	protected void receivePacket(int key, EmptyPacket deserialized) throws IOException {
		if (key == NetworkConstants.Keys.REQUEST_MATCHES) {
			serverManager.sendJoinableMatches(player);
		} else if (key == NetworkConstants.Keys.REQUEST_PLAYERS_RUNNING_MATCHES) {
			serverManager.sendJoinableRunningMatches(player);
		}
	}
}
