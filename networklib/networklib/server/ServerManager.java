package networklib.server;

import java.util.List;

import networklib.NetworkConstants;
import networklib.channel.Channel;
import networklib.client.exceptions.InvalidStateException;
import networklib.server.db.IDBFacade;
import networklib.server.game.Match;
import networklib.server.game.Player;
import networklib.server.listeners.identify.IdentifyUserListener;
import networklib.server.listeners.matches.RequestLeaveMatchListener;
import networklib.server.listeners.matches.RequestMatchesListener;
import networklib.server.listeners.matches.RequestOpenNewMatchListener;
import networklib.server.packets.ArrayOfMatchInfosPacket;
import networklib.server.packets.MatchInfoPacket;
import networklib.server.packets.OpenNewMatchPacket;
import networklib.server.packets.RejectPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ServerManager implements IServerManager {

	private final IDBFacade db;

	public ServerManager(IDBFacade db) {
		this.db = db;
	}

	public void identifyNewChannel(Channel channel) {
		channel.registerListener(new IdentifyUserListener(channel, this));
	}

	@Override
	public boolean acceptNewPlayer(Player player) {
		if (db.isAcceptedPlayer(player.getId())) {
			db.storePlayer(player);

			Channel channel = player.getChannel();
			channel.removeListener(NetworkConstants.Keys.IDENTIFY_USER);

			channel.setChannelClosedListener(new ServerChannelClosedListener(this, player));
			channel.registerListener(new RequestMatchesListener(this, player));
			channel.registerListener(new RequestOpenNewMatchListener(this, player));
			channel.registerListener(new RequestLeaveMatchListener(this, player));

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void sendJoinableMatches(Player player) {
		List<Match> matches = db.getJoinableMatches();

		sendMatchesList(player, matches);
	}

	private void sendMatchesList(Player player, List<Match> matches) {
		MatchInfoPacket[] matchInfoPackets = new MatchInfoPacket[matches.size()];
		int i = 0;
		for (Match curr : matches) {
			matchInfoPackets[i] = new MatchInfoPacket(curr);
			i++;
		}

		player.sendPacket(new ArrayOfMatchInfosPacket(matchInfoPackets));
	}

	@Override
	public void sendJoinableRunningMatches(Player player) {
		List<Match> matches = db.getJoinableRunningMatches(player);

		sendMatchesList(player, matches);
	}

	@Override
	public void channelClosed(Player player) {
		db.removePlayer(player);
		Match match = db.getRunningMatchOf(player);
		if (match != null) {
			match.notifyPlayerLeft(player);
		}
	}

	@Override
	public void createNewMatch(OpenNewMatchPacket matchInfo, Player player) {
		Match match = new Match(matchInfo.getMatchName(), matchInfo.getMaxPlayers(), matchInfo.getMapInfo());
		db.storeMatch(match);

		try {
			player.joinMatch(match);
		} catch (InvalidStateException e) {
			e.printStackTrace();
			player.getChannel().sendPacket(
					new RejectPacket(NetworkConstants.Strings.INVALID_STATE_ERROR, NetworkConstants.Keys.REQUEST_OPEN_NEW_MATCH));
		}
	}

	@Override
	public void leaveMatch(Player player) {
		try {
			player.leaveMatch();
		} catch (InvalidStateException e) {
			e.printStackTrace();
		}
	}

}
