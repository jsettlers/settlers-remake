package networklib.server;

import java.util.List;

import networklib.NetworkConstants;
import networklib.channel.Channel;
import networklib.channel.IChannelClosedListener;
import networklib.client.exceptions.InvalidStateException;
import networklib.server.actions.identify.IUserAcceptor;
import networklib.server.actions.identify.IdentifyUserListener;
import networklib.server.actions.matches.IMatchesSupplier;
import networklib.server.actions.matches.INewMatchCreator;
import networklib.server.actions.matches.RequestMatchesListener;
import networklib.server.actions.matches.RequestOpenNewMatchListener;
import networklib.server.actions.packets.ArrayOfMatchInfosPacket;
import networklib.server.actions.packets.MatchInfoPacket;
import networklib.server.actions.packets.OpenNewMatchPacket;
import networklib.server.actions.packets.RejectPacket;
import networklib.server.db.IDBFacade;
import networklib.server.game.Match;
import networklib.server.game.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ServerManager implements IUserAcceptor, IMatchesSupplier, INewMatchCreator {

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

			channel.setChannelClosedListener(new ServerChannelClosedListener(player));
			channel.registerListener(new RequestMatchesListener(this, player));
			channel.registerListener(new RequestOpenNewMatchListener(this, player));

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

	void channelClosed(Player player) {
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

			player.getChannel().sendPacket(new MatchInfoPacket(match));
		} catch (InvalidStateException e) {
			e.printStackTrace();
			player.getChannel().sendPacket(
					new RejectPacket(NetworkConstants.Strings.INVALID_STATE_ERROR, NetworkConstants.Keys.REQUEST_OPEN_NEW_MATCH));
		}
	}

	private class ServerChannelClosedListener implements IChannelClosedListener {
		private final Player player;

		public ServerChannelClosedListener(Player player) {
			this.player = player;
		}

		@Override
		public void channelClosed() {
			ServerManager.this.channelClosed(player);
		}
	}

}
