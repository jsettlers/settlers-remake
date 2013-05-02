package networklib.server.actions.matches;

import networklib.server.actions.packets.OpenNewMatchPacket;
import networklib.server.game.Player;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface INewMatchCreator {

	/**
	 * Creates a new match with the given name and the given map for the given {@link Player} and joins the player to the match.
	 * 
	 * @param matchInfo
	 *            An {@link OpenNewMatchPacket} containing the data to be used for creating the new match.
	 * @param player
	 *            The player that want's to create the match. This player will directly be joined into the match.
	 */
	void createNewMatch(OpenNewMatchPacket matchInfo, Player player);

}
