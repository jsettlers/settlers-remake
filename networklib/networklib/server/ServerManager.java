package networklib.server;

import java.util.List;

import networklib.NetworkConstants;
import networklib.channel.Channel;
import networklib.channel.IChannelClosedListener;
import networklib.server.actions.identify.IUserAcceptor;
import networklib.server.actions.matches.IMatchesSupplier;
import networklib.server.actions.matches.INewMatchCreator;
import networklib.server.actions.matches.RequestMatchesListener;
import networklib.server.actions.matches.RequestOpenNewMatchListener;
import networklib.server.actions.packets.OpenNewMatchPacket;
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

	@Override
	public boolean acceptNewPlayer(Player player) {
		if (db.isAcceptedPlayer(player.getId())) {
			db.storePlayer(player);

			Channel channel = player.getChannel();
			channel.removeListener(NetworkConstants.Keys.IDENTIFY_USER);

			channel.setChannelClosedListener(new ServerChannelClosedListener(player));
			channel.registerListener(new RequestMatchesListener(channel, this, player));
			channel.registerListener(new RequestOpenNewMatchListener(this, player));

			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Match> getJoinableMatches() {
		return db.getJoinableMatches();
	}

	@Override
	public List<Match> getJoinableRunningMatches(Player player) {
		return db.getJoinableRunningMatches(player);
	}

	void channelClosed(Player player) {
		db.removePlayer(player);
		Match match = db.getRunningMatchOf(player);
		if (match != null) {
			match.notifyPlayerLeft(player);
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

	@Override
	public Match createNewMatch(OpenNewMatchPacket matchInfo, Player player) {
		Match match = new Match(matchInfo.getMatchName(), matchInfo.getMaxPlayers(), matchInfo.getMapInfo());
		db.storeMatch(match);

		match.joinPlayer(player);
		return match;
	}
}
