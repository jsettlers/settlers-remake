package networklib.server.listeners.matches;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.IDeserializingable;
import networklib.channel.listeners.PacketChannelListener;
import networklib.server.IServerManager;
import networklib.server.game.Player;
import networklib.server.packets.KeyOnlyPacket;

/**
 * This {@link PacketChannelListener} handles requests for the matches list and the list of running games of the player.
 * 
 * @author Andreas Eberle
 * 
 */
public class RequestMatchesListener extends PacketChannelListener<KeyOnlyPacket> {

	private final IServerManager serverManager;
	private final Player player;

	@SuppressWarnings("unchecked")
	public RequestMatchesListener(IServerManager serverManager, Player player) {
		super(new int[] { NetworkConstants.Keys.REQUEST_MATCHES, NetworkConstants.Keys.REQUEST_PLAYERS_RUNNING_MATCHES },
				new IDeserializingable[] { KeyOnlyPacket.DEFAULT_DESERIALIZER, KeyOnlyPacket.DEFAULT_DESERIALIZER });

		this.serverManager = serverManager;
		this.player = player;
	}

	@Override
	protected void receivePacket(KeyOnlyPacket deserialized) throws IOException {
		if (deserialized.getKey() == NetworkConstants.Keys.REQUEST_MATCHES) {
			serverManager.sendJoinableMatches(player);
		} else if (deserialized.getKey() == NetworkConstants.Keys.REQUEST_PLAYERS_RUNNING_MATCHES) {
			serverManager.sendJoinableRunningMatches(player);
		}
	}
}
