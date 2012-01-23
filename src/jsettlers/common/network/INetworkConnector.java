package jsettlers.common.network;

import java.io.IOException;

/**
 * Interface not needed by jsettlers.graphics to interact with the networking system.
 * 
 * @author Andreas Eberle
 * 
 */
public interface INetworkConnector {
	/**
	 * Connect to the given host.
	 * 
	 * @param host
	 *            host address
	 */
	void connectToServer(String host) throws IOException;

	/**
	 * Cancel the connection to the server.
	 */
	void cancelConnection();

	/**
	 * Sets the players name on the server.
	 * 
	 * @param playerName
	 *            name of the player
	 */
	void setPlayerName(String playerName);

	/**
	 * Start a new match with given settings and the given map.
	 * 
	 * @param settings
	 *            settings for the game.
	 * @param map
	 *            map of the match
	 */
	void startMatch(IMatchSettings settings, INetworkableMap map);

	/**
	 * leave the match, the user is currently playing.
	 */
	void leaveCurrentMatch();

	/**
	 * Needs to be called to cause the client to load the matches list or refresh it.<br>
	 * The listener will be informed when the list is available.
	 */
	void refreshMatchesList();

	/**
	 * Get list of available matches.
	 * 
	 * @return list of available matches.
	 */
	IMatch[] getMatches();

	/**
	 * Join the given match.
	 * 
	 * @param match
	 *            match to be joined.
	 */
	void joinMatch(IMatch match);

	/**
	 * NOTE: only one listener can be set at a time!
	 * 
	 * @param listener
	 */
	void setListener(INetworkConnectorListener listener);

	/**
	 * remove the listener.
	 */
	void removeListener();

	/**
	 * 
	 * @return an array of names of the match attendants. The index in the array is the players number. The array always has the length of the maximum
	 *         players allowed in the match.
	 */
	String[] getMatchAttendants();

	/**
	 * Needs to be called to cause the client to load the match attendants list or refresh it.<br>
	 * The listener will be informed when the list is available.
	 */
	void refreshMatchAttendants();
}
