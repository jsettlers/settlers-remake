package networklib.server.db.inMemory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import networklib.server.db.IDBFacade;
import networklib.server.match.EPlayerState;
import networklib.server.match.Match;
import networklib.server.match.Player;

/**
 * This class implements an in memory database.
 * 
 * @author Andreas Eberle
 * 
 */
public class InMemoryDB implements IDBFacade {

	private HashMap<String, Player> players = new HashMap<String, Player>();
	private HashMap<String, Match> matches = new HashMap<String, Match>();

	@Override
	public boolean isAcceptedPlayer(String id) {
		return true;
	}

	@Override
	public void storePlayer(Player player) {
		synchronized (players) {
			players.put(player.getId(), player);
		}
	}

	@Override
	public void removePlayer(Player player) {
		synchronized (players) {
			players.remove(player.getId());
		}
	}

	@Override
	public Match getRunningMatchOf(Player player) {
		synchronized (matches) {
			for (Match curr : matches.values()) {
				if (curr.isRunning() && curr.hasPlayer(player)) {
					return curr;
				}
			}
		}

		return null;
	}

	@Override
	public List<Match> getJoinableMatches() {
		List<Match> result = new LinkedList<Match>();

		synchronized (matches) {
			for (Match curr : matches.values()) {
				if (curr.canJoin()) {
					result.add(curr);
				}
			}
		}

		return result;
	}

	@Override
	public List<Match> getJoinableRunningMatches(Player player) {
		List<Match> result = new LinkedList<Match>();

		synchronized (matches) {
			for (Match curr : matches.values()) {
				if (curr.isRunning() && curr.hasLeftPlayer(player.getId())) {
					result.add(curr);
				}
			}
		}

		return result;
	}

	@Override
	public void storeMatch(Match match) {
		matches.put(match.getId(), match);
	}

	public int getNumberOfPlayers() {
		return players.size();
	}

	public Player getPlayer(String id) {
		return players.get(id);
	}

	public int getNumberOfMatches() {
		return matches.size();
	}

	@Override
	public Match getMatchById(String id) {
		return matches.get(id);
	}

	@Override
	public List<Player> getPlayers(EPlayerState... allowedStates) {
		synchronized (players) {
			List<Player> result = new LinkedList<Player>();
			for (Player curr : players.values()) {
				if (EPlayerState.isOneOf(curr.getState(), allowedStates)) {
					result.add(curr);
				}
			}
			return result;
		}
	}
}
