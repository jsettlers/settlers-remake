package jsettlers.graphics.startscreen.interfaces;

/**
 * This interface offers the methods that interact with a multiplayer server.
 * 
 * @author Andreas Eberle
 */
public interface IMultiplayerConnector {

	/**
	 * Gets a list of multiplayer games that can be joined.
	 * 
	 * @param onServer
	 *            The name of the server we should search on.
	 * @return
	 */
	IChangingList<? extends IJoinableGame> getJoinableMultiplayerGames();

	/**
	 * Joins the given multiplayer game.
	 * 
	 * @param game
	 * @return
	 */
	IJoiningGame joinMultiplayerGame(IJoinableGame game)
	        throws IllegalStateException;

	/**
	 * Creates a new multiplayer game on the server and joins this new game.
	 * 
	 * @param gameInfo
	 *            {@link IOpenMultiplayerGameInfo} object defining the
	 *            parameters of the game.
	 * @return
	 */
	IJoiningGame openNewMultiplayerGame(IOpenMultiplayerGameInfo gameInfo);

	/**
	 * @return Returns the {@link EMultiplayerConnectorState} of this connector.
	 */
	EMultiplayerConnectorState getState();

	/**
	 * @return Returns the round trip time in milli seconds.
	 */
	int getRoundTripTimeInMs();

	/**
	 * Shuts down the connection to the server and stops the threads this
	 * multiplayer connector started.
	 */
	void shutdown();
}
