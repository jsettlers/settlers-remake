package jsettlers.graphics.startscreen;

import java.util.Date;

import jsettlers.common.map.IMapDataProvider;

/**
 * This connector provides data that is needed by the start screen.
 * 
 * @author michael
 */
public interface IStartScreenConnector {
	/**
	 * Gets a list of maps the user can play.
	 * 
	 * @return
	 */
	IMapItem[] getMaps();

	public interface IMapItem {
		String getName();

		int getMinPlayers();

		int getMaxPlayers();

		/**
		 * Creates a loadable game. This method should return fast.
		 * <p>
		 * It does not fail, but may return a map data provider that fails
		 * loading.
		 * 
		 * @return never null.
		 */
		IMapDataProvider createLoadableGame(int players, long random);
	}

	/**
	 * Gets the games a user may load
	 * 
	 * @return A fixed list of games.
	 */
	ILoadableGame[] getLoadableGames();

	public interface ILoadableGame {
		String getName();

		Date getSaveTime();
	}

	/**
	 * Gets a list of network games that can be recovered.
	 */
	IRecoverableGame[] getRecoverableGames();

	public interface IRecoverableGame extends INetworkGame {
		Date getDate();
	}

	/**
	 * Gets a list of network games.
	 * 
	 * @return The list of network games.
	 */
	INetworkGame[] getNetworkGames();

	public interface INetworkGame {
		String getName();

		String[] getPlayerNames();
	}

	void setNetworkServer(String host);

	void addNetworkGameListener(INetworkGameListener gameListener);

	void removeNetworkGameListener(INetworkGameListener gameListener);

	public interface INetworkGameListener {
		void networkGamesChanged();
	}

	/* - - - - - callbacks - - - - - */
	/**
	 * Called when the user starts a singleplayer game
	 */
	void startNewGame(IGameSettings game);

	public interface IGameSettings {
		IMapItem getMap();

		int getPlayerCount();
	}

	/**
	 * Called when the user wants to load a game
	 * 
	 * @param load
	 *            The game to load. A item of getLoadableGames();
	 */
	void loadGame(ILoadableGame load);

	/**
	 * Starts a network game
	 * 
	 * @param game
	 * @param name
	 */
	void startGameServer(IGameSettings game, String name);

	void recoverNetworkGame(IRecoverableGame game);

	void joinNetworkGame(INetworkGame game);

	void exitGame();
}
