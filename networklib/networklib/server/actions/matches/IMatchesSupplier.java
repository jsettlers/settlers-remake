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
	 * @return Returns a list of running matches where the given player had already participated.
	 */
	List<Match> getJoinableRunningMatches(Player player);

}
