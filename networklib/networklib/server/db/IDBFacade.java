package networklib.server.db;

import networklib.server.actions.matches.IMatchesSupplier;
import networklib.server.game.Match;
import networklib.server.game.Player;

/**
 * This interface defines the methods required by the server to use a database.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IDBFacade extends IMatchesSupplier {

	/**
	 * Checks if the player is allowed to connect to the server.
	 * 
	 * @param id
	 *            The id of the player.
	 * @return Returns true if the player may connect to the server.
	 */
	boolean isAcceptedPlayer(String id);

	void storePlayer(Player player);

	void removePlayer(Player player);

	/**
	 * 
	 * @param player
	 * @return Returns the running {@link Match} of the given {@link Player} <br>
	 *         or null if the {@link Player} has no running {@link Match}.
	 */
	Match getRunningMatchOf(Player player);

	void storeMatch(Match match);

}
