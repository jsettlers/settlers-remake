package networklib.server.actions.matches;

import networklib.server.game.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface IMatchesSupplier {

	void sendJoinableMatches(Player player);

	/**
	 * 
	 * @param player
	 * @return Returns a list of running matches where the given player had already participated.
	 */
	void sendJoinableRunningMatches(Player player);

}
