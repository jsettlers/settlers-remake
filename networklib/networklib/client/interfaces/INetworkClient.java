package networklib.client.interfaces;

import networklib.client.receiver.IPacketReceiver;
import networklib.common.packets.ArrayOfMatchInfosPacket;
import networklib.common.packets.ChatMessagePacket;
import networklib.common.packets.MapInfoPacket;
import networklib.common.packets.MatchInfoPacket;
import networklib.common.packets.MatchInfoUpdatePacket;
import networklib.common.packets.MatchStartPacket;
import networklib.common.packets.PlayerInfoPacket;
import networklib.infrastructure.channel.reject.RejectPacket;
import networklib.server.game.EPlayerState;

/**
 * This interface defines the methods offered by the client of the networklib.
 * 
 * @author Andreas Eberle
 * 
 */
public interface INetworkClient extends ITaskScheduler {

	/**
	 * Tries to authenticate the user at the server and registers the given {@link IPacketReceiver} to receive the list of joinable matches. The
	 * receiver will be called with a new list of matches form time to time.
	 * 
	 * @param id
	 *            The id of the user. This must be a globally unique number and it must always be the same for the same player.
	 * @param name
	 *            The displayed name of the player. The name can always be changed.
	 * @param matchListReceiver
	 *            The receiver that gets the list of joinable matches.
	 * @throws InvalidStateException
	 *             This exception might be thrown, if the {@link #logIn(String, String, IPacketReceiver)} operation is called when the
	 *             {@link INetworkClient} is already logged in to the server.
	 */
	void logIn(String id, String name, IPacketReceiver<ArrayOfMatchInfosPacket> matchListReceiver) throws IllegalStateException;

	/**
	 * Opens a new match on the server.
	 * 
	 * @param matchName
	 *            Name of the new match.
	 * @param maxPlayers
	 *            The maximum number of players that can join the match.
	 * @param mapInfo
	 *            The map of the match.
	 * @param randomSeed
	 *            The random seed used for the match.
	 * @param matchStartedListener
	 *            The listener that will be called, when the match starts.
	 * @param matchInfoUpdatedListener
	 *            The listener that will be called, when there are updates on the players or the match informations. For example when a player joined
	 *            or left.
	 * @param chatMessageReceiver
	 *            The receiver for chat messages. It will be called with any received chat message.
	 * @throws InvalidStateException
	 *             This exception might be thrown, if the client is either not logged in to the server (see
	 *             {@link #logIn(String, String, IPacketReceiver)}) or if the client is already in a match.
	 */
	void openNewMatch(String matchName, int maxPlayers, MapInfoPacket mapInfo, long randomSeed,
			IPacketReceiver<MatchStartPacket> matchStartedListener, IPacketReceiver<MatchInfoUpdatePacket> matchInfoUpdatedListener,
			IPacketReceiver<ChatMessagePacket> chatMessageReceiver)
			throws IllegalStateException;

	/**
	 * 
	 * @param match
	 * @param matchStartedListener
	 * @param matchInfoUpdatedListener
	 * @param chatMessageReceiver
	 * @throws InvalidStateException
	 */
	void joinMatch(String matchId, IPacketReceiver<MatchStartPacket> matchStartedListener,
			IPacketReceiver<MatchInfoUpdatePacket> matchInfoUpdatedListener, IPacketReceiver<ChatMessagePacket> chatMessageReceiver)
			throws IllegalStateException;

	void startMatch() throws IllegalStateException;

	void setReadyState(boolean ready) throws IllegalStateException;

	void sendChatMessage(String message) throws IllegalStateException;

	void leaveMatch();

	void registerRejectReceiver(IPacketReceiver<RejectPacket> rejectListener);

	EPlayerState getState();

	MatchInfoPacket getMatchInfo();

	PlayerInfoPacket getPlayerInfo();

	void close();
}
