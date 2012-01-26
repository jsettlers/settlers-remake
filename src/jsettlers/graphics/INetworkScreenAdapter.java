package jsettlers.graphics;

import java.util.List;

public interface INetworkScreenAdapter {

	void setListener(INetworkScreenListener networkScreen);
	
	/**
	 * Gets a immutable list of players.
	 * @return The current players.
	 */
	List<INetworkPlayer> getPlayerList();

	interface INetworkScreenListener {
		void playerListChanged();
		
		void addChatMessage(String message);
	}
	
	interface INetworkPlayer {
		String getPlayerName();
	}

	void setStartAllowed(boolean startAllowed);

	void sendChatMessage(String message);

	void startGame();
}
