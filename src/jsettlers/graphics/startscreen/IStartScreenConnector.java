package jsettlers.graphics.startscreen;

import java.util.Date;
import java.util.List;

import jsettlers.common.network.INetworkConnector;

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
	List<? extends IMapItem> getMaps();

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
		// IMapDataProvider createLoadableGame(int players, long random);
	}

	/**
	 * Gets the games a user may load
	 * 
	 * @return A fixed list of games.
	 */
	List<? extends ILoadableGame> getLoadableGames();

	public interface ILoadableGame {
		String getName();

		Date getSaveTime();
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

	void exitGame();

	INetworkConnector getNetworkConnector();
}
