package networklib.server.actions.matches;

import java.util.List;

import networklib.server.game.Match;
import networklib.server.game.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface IMatchesSupplier {

	List<Match> getJoinableMatches();

	/**
	 * 
	 * @param player
	 * @return Returns a list of matches that are already running and the given player can join.
	 */
	List<Match> getJoinableRunningMatches(Player player);

}
