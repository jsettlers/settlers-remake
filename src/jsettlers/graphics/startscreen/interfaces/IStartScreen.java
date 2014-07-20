package jsettlers.graphics.startscreen.interfaces;

import jsettlers.common.utils.collections.ChangingList;

/**
 * This is the main start screen interface. It is given to the start screen when the screen is created.
 * <p>
 * This interface provides access to methods for getting a list of startable games.
 * 
 * @author michael
 */
public interface IStartScreen {

	/**
	 * Gets a list of installed maps. The list may change after this call, e.g. because it was not fully loaded. This only returns maps that can be
	 * played in singleplayer.
	 * 
	 * @return The list of installed and therefore startable maps.
	 */
	ChangingList<IMapDefinition> getSingleplayerMaps();

	/**
	 * Gets a list of games that were saved in singleplayer mode.
	 * 
	 * @return The list.
	 */
	ChangingList<IMapDefinition> getStoredSingleplayerGames();

	/**
	 * Gets the maps for which a new multiplayer game can be started.
	 * 
	 * @return The list of maps.
	 */
	ChangingList<IMapDefinition> getMultiplayerMaps();

	/**
	 * Gets a list of saved multiplayer games which we can restore.
	 * 
	 * @return
	 */
	ChangingList<IMapDefinition> getRestorableMultiplayerGames();

	/**
	 * TODO: Add map settings.
	 * 
	 * @param map
	 * @param listener
	 */
	IStartingGame startSingleplayerGame(IMapDefinition map);

	IStartingGame loadSingleplayerGame(IMapDefinition map);

	/**
	 * Opens a new connection to the server at the given address.
	 * 
	 * @param serverAddr
	 *            Address of the server.
	 * @param player
	 *            The player that want's to access the server.
	 * @return A new instance of a {@link IMultiplayerConnector}.
	 */
	IMultiplayerConnector getMultiplayerConnector(String serverAddr,
			Player player);

	public static final IStartScreen DEFAULT_IMPLEMENTATION = new IStartScreen() {

		@Override
		public ChangingList<IMapDefinition> getSingleplayerMaps() {
			return new ChangingList<IMapDefinition>();
		}

		@Override
		public ChangingList<IMapDefinition> getStoredSingleplayerGames() {
			return new ChangingList<IMapDefinition>();
		}

		@Override
		public ChangingList<IMapDefinition> getMultiplayerMaps() {
			return new ChangingList<IMapDefinition>();
		}

		@Override
		public ChangingList<IMapDefinition> getRestorableMultiplayerGames() {
			return new ChangingList<IMapDefinition>();
		}

		@Override
		public IStartingGame startSingleplayerGame(IMapDefinition map) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IStartingGame loadSingleplayerGame(IMapDefinition map) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IMultiplayerConnector getMultiplayerConnector(String serverAddr, Player player) {
			// TODO Auto-generated method stub
			return null;
		}

	};
}
