package networklib.server.actions.matches;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import networklib.channel.Channel;
import networklib.channel.IDeserializingable;
import networklib.channel.NetworkConstants;
import networklib.channel.listeners.PacketChannelListener;
import networklib.server.actions.packets.KeyOnlyPacket;
import networklib.server.actions.packets.ArrayOfMatchInfosPacket;
import networklib.server.actions.packets.MatchInfoPacket;
import networklib.server.game.Match;
import networklib.server.game.Player;

/**
 * This {@link PacketChannelListener} handles requests for the matches list and the list of running games of the player.
 * 
 * @author Andreas Eberle
 * 
 */
public class GetMatchesListener extends PacketChannelListener<KeyOnlyPacket> {

	private final IMatchesSupplier matchesSupplier;
	private final Channel channel;
	private final Player player;

	@SuppressWarnings("unchecked")
	public GetMatchesListener(Channel channel, IMatchesSupplier matchesSupplier, Player player) {
		super(new int[] { NetworkConstants.Keys.GET_MATCHES, NetworkConstants.Keys.GET_PLAYERS_RUNNING_MATCHES },
				new IDeserializingable[] { KeyOnlyPacket.DEFAULT_DESERIALIZER, KeyOnlyPacket.DEFAULT_DESERIALIZER });

		this.channel = channel;
		this.matchesSupplier = matchesSupplier;
		this.player = player;
	}

	@Override
	protected void receivePacket(KeyOnlyPacket deserialized) throws IOException {
		List<Match> matches;

		if (deserialized.getKey() == NetworkConstants.Keys.GET_MATCHES) {
			matches = matchesSupplier.getJoinableMatches();
		} else if (deserialized.getKey() == NetworkConstants.Keys.GET_PLAYERS_RUNNING_MATCHES) {
			matches = matchesSupplier.getJoinableRunningMatches(player);
		} else {
			matches = new LinkedList<Match>();
		}

		MatchInfoPacket[] matchInfoPackets = new MatchInfoPacket[matches.size()];
		int i = 0;
		for (Match curr : matches) {
			matchInfoPackets[i] = new MatchInfoPacket(curr);
			i++;
		}

		channel.sendPacket(new ArrayOfMatchInfosPacket(matchInfoPackets));
	}
}
