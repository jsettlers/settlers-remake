package jsettlers.main;

import jsettlers.common.network.IMatch;
import jsettlers.common.network.IMatchSettings;
import jsettlers.graphics.startscreen.IStartScreenConnector.IGameSettings;
import jsettlers.graphics.startscreen.IStartScreenConnector.ILoadableGame;

public interface IGameStarter {

	public abstract void startGame(IGameSettings game);

	public abstract void loadGame(ILoadableGame load);

	/**
	 * Starts a new network game server.
	 * @param gameSettings
	 */
	void openNetworkGame(IMatchSettings gameSettings);

	/**
	 * Joins a network game
	 * @param serverAddress The server.
	 * @param match
	 */
	void joinNetworkGame(String serverAddress, IMatch match);

}