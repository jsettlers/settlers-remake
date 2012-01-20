package jsettlers.common.network;

import java.io.IOException;

/**
 * interface for user of INetworkConnector to get callbacks.
 * 
 * @author Andreas Eberle
 * 
 */
public interface INetworkConnectorListener {
	/**
	 * list of matches is now available / refreshed.
	 */
	void retrievedMatches();

	/**
	 * called if a request fails
	 */
	void requestFailed(String request);

	/**
	 * called when the client successfully joined a match.
	 */
	void joinedMatch();

	/**
	 * called when we received the map from the server
	 */
	void receivedMap();

	/**
	 * called if an exception occurred and the connection to the server was lost.
	 * 
	 * @param e
	 *            catched exception
	 */
	void connectionLost(IOException e);

	/**
	 * list of attendants is now available / refreshed.
	 */
	void retrievedMatchAttendants();
}
