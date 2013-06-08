package jsettlers.graphics.startscreen.interfaces;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * This is the main start screen interface. It is given to the start screen when
 * the screen is created.
 * <p>
 * This interface provides access to methods for getting a list of startable
 * games.
 * 
 * @author michael
 */
public interface IStartScreen {
	/**
	 * Gets a list of installed maps. The list may change after this call, e.g.
	 * because it was not fully loaded. This only returns maps that can be
	 * played in singleplayer.
	 * 
	 * @return The list of installed and therefore startable maps.
	 */
	IChangingList<IStartableMapDefinition> getSingleplayerMaps();

	/**
	 * Gets a list of games that were saved in singleplayer mode.
	 * 
	 * @return The list.
	 */
	IChangingList<ILoadableMapDefinition> getStoredSingleplayerGames();

	/**
	 * Gets the maps for which a new multiplayer game can be started.
	 * 
	 * @return The list of maps.
	 */
	IChangingList<IStartableMapDefinition> getMultiplayerMaps();

	/**
	 * Gets a list of saved multiplayer games which we can restore.
	 * 
	 * @return
	 */
	IChangingList<ILoadableMapDefinition> getRestorableMultiplayerGames();

	/**
	 * TODO: Add map settings.
	 * 
	 * @param map
	 * @param listener
	 */
	IStartingGame startSingleplayerGame(IStartableMapDefinition map);

	IStartingGame loadSingleplayerGame(ILoadableMapDefinition map);

	/**
	 * Opens a new connection to the server at the given address.
	 * 
	 * @param serverAddr
	 *            Address of the server.
	 * @param player
	 *            The player that want's to access the server.
	 * @return A new instance of a {@link IMultiplayerConnector}.
	 * @throws UnknownHostException
	 *             This exception is thrown if the given server can not be
	 *             found.
	 * @throws IOException
	 *             This exception might be thrown if there was an error during
	 *             the establishing of the connection.
	 */
	IMultiplayerConnector getMultiplayerConnector(String serverAddr,
	        Player player) throws UnknownHostException, IOException;
}
