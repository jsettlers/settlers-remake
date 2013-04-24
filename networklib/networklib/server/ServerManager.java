package networklib.server;

import java.util.List;

import networklib.channel.Channel;
import networklib.channel.NetworkConstants;
import networklib.server.actions.identify.IUserAcceptor;
import networklib.server.actions.matches.GetMatchesListener;
import networklib.server.actions.matches.IMatchesSupplier;
import networklib.server.db.IDBFacade;
import networklib.server.game.Match;
import networklib.server.game.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class ServerManager implements IUserAcceptor, IMatchesSupplier {

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
			channel.registerListener(new GetMatchesListener(channel, this, player));

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
}
