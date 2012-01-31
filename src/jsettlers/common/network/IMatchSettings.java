package jsettlers.common.network;

public interface IMatchSettings {
	/**
	 * 
	 * @return name of the match.
	 */
	String getMatchName();

	/**
	 * 
	 * @return maximum players allowed in the match
	 */
	int getMaxPlayers();

	/**
	 * 
	 * @return random seed for all players.
	 */
	long getRandomSeed();

	INetworkableMap getMap();
	
	/**
	 * Gets the address of the server to open the game on.
	 * @return The server address.
	 */
	String getServerAddress();
}
