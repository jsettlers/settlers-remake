package jsettlers.common.network;

/**
 * interface for user of INetworkConnector to get callbacks.
 * 
 * @author Andreas Eberle
 * 
 */
public interface INetworkConnectorListener {
	/**
	 * list of matches is now available.
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
}
