package networklib.server.actions.matches;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.GenericDeserializer;
import networklib.channel.listeners.PacketChannelListener;
import networklib.server.actions.packets.MatchInfoPacket;
import networklib.server.actions.packets.OpenNewMatchPacket;
import networklib.server.actions.packets.RejectPacket;
import networklib.server.game.Match;
import networklib.server.game.Player;

/**
 * This listener is called when a client request to open up a new {@link Match}. After the match has successfully been created, the client will
 * receive a {@link MatchInfoPacket}.
 * 
 * @author Andreas Eberle
 * 
 */
public class RequestOpenNewMatchListener extends PacketChannelListener<OpenNewMatchPacket> {

	private final INewMatchCreator matchCreator;
	private final Player player;

	public RequestOpenNewMatchListener(INewMatchCreator matchCreator, Player player) {
		super(NetworkConstants.Keys.REQUEST_OPEN_NEW_MATCH, new GenericDeserializer<OpenNewMatchPacket>(OpenNewMatchPacket.class));
		this.matchCreator = matchCreator;
		this.player = player;
	}

	@Override
	protected void receivePacket(OpenNewMatchPacket packet) throws IOException {
		Match match = matchCreator.createNewMatch(packet, player);

		if (match != null) {
			player.getChannel().sendPacket(new MatchInfoPacket(match));
		} else {
			player.getChannel().sendPacket(new RejectPacket(NetworkConstants.Strings.UNKNOWN_ERROR, NetworkConstants.Keys.REQUEST_OPEN_NEW_MATCH));
		}
	}

}
