package networklib.server.actions.matches;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.IDeserializingable;
import networklib.channel.listeners.PacketChannelListener;
import networklib.server.actions.packets.KeyOnlyPacket;
import networklib.server.game.Player;

/**
 * This {@link PacketChannelListener} handles requests for the matches list and the list of running games of the player.
 * 
 * @author Andreas Eberle
 * 
 */
public class RequestMatchesListener extends PacketChannelListener<KeyOnlyPacket> {

	private final IMatchesSupplier matchesSupplier;
	private final Player player;

	@SuppressWarnings("unchecked")
	public RequestMatchesListener(IMatchesSupplier matchesSupplier, Player player) {
		super(new int[] { NetworkConstants.Keys.REQUEST_MATCHES, NetworkConstants.Keys.REQUEST_PLAYERS_RUNNING_MATCHES },
				new IDeserializingable[] { KeyOnlyPacket.DEFAULT_DESERIALIZER, KeyOnlyPacket.DEFAULT_DESERIALIZER });

		this.matchesSupplier = matchesSupplier;
		this.player = player;
	}

	@Override
	protected void receivePacket(KeyOnlyPacket deserialized) throws IOException {
		if (deserialized.getKey() == NetworkConstants.Keys.REQUEST_MATCHES) {
			matchesSupplier.sendJoinableMatches(player);
		} else if (deserialized.getKey() == NetworkConstants.Keys.REQUEST_PLAYERS_RUNNING_MATCHES) {
			matchesSupplier.sendJoinableRunningMatches(player);
		}
	}
}
