package networklib.server;

import networklib.server.game.Player;
import networklib.server.packets.ChatMessagePacket;
import networklib.server.packets.MatchInfoPacket;
import networklib.server.packets.OpenNewMatchPacket;
import networklib.server.packets.TimeSyncPacket;

public interface IServerManager {

	boolean acceptNewPlayer(Player player);

	void leaveMatch(Player player);

	/**
	 * Creates a new match with the given name and the given map for the given {@link Player} and joins the player to the match.
	 * 
	 * @param matchInfo
	 *            An {@link OpenNewMatchPacket} containing the data to be used for creating the new match.
	 * @param player
	 *            The player that want's to create the match. This player will directly be joined into the match.
	 */
	void createNewMatch(OpenNewMatchPacket matchInfo, Player player);

	void sendJoinableMatches(Player player);

	/**
	 * 
	 * @param player
	 * @return Returns a list of running matches where the given player had already participated.
	 */
	void sendJoinableRunningMatches(Player player);

	void channelClosed(Player player);

	/**
	 * Starts the match of the given player.
	 * 
	 * @param player
	 */
	void startMatch(Player player);

	void forwardChatMessage(Player player, ChatMessagePacket packet);

	/**
	 * Sends the given {@link TimeSyncPacket} to the other players in the {@link Player}s match.
	 * 
	 * @param player
	 *            The player that sent the {@link TimeSyncPacket}.
	 * @param packet
	 */
	void distributeTimeSync(Player player, TimeSyncPacket packet);

	void joinMatch(MatchInfoPacket packet, Player player);
}
